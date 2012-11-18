using System;
using System.Collections;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Web.Services;

namespace PFMWebService
{
    /// <summary>
    /// Summary description for PFMService
    /// </summary>
    [WebService(Namespace = "http://tempuri.org/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class PfmService : WebService
    {
        readonly PFMDataClassesDataContext _context = new PFMDataClassesDataContext();
        readonly Dictionary<string, string> _tableMap = new Dictionary<string, string>();
        const string CONNECTION_STRING = "Data Source=localhost;Initial Catalog=PFMDatabase;Integrated Security=True";
        readonly SqlConnection conn;
        readonly SqlCommand command;
        public PfmService()
        {
            conn = new SqlConnection(CONNECTION_STRING);
            command = conn.CreateCommand();
            String[] sTables = { "Schedule", "ScheduleDetail", "EntryDetail", "Entry", "BorrowLend", "Category" };
            String[] sColumns = {"Id, CreatedDate, ModifiedDate, Budget, Type, IsDeleted, StartDate, EndDate", // Schedule
                                 "Id, CreatedDate, ModifiedDate, Budget, IsDeleted, CategoryID, ScheduleID", // Schedule Detail
                                 "Id, CreatedDate, ModifiedDate, CategoryID, Name, IsDeleted, Money, EntryID", // Entry Detail
                                 "Id, CreatedDate, ModifiedDate, IsDeleted, Date, Type", // Entry
                                 "Id, CreatedDate, ModifiedDate, IsDeleted, DebtType, Money, InterestType, InterestRate, StartDate, ExpiredDate, PersonName, PersonPhone, PersonAddress", //BorrowLend
                                 "Id, CreatedDate, ModifiedDate, Name, IsDeleted, UserColor"}; //Category

            for (var index = 0; index < sTables.Count(); index++)
            {
                _tableMap[sTables[index]] = sColumns[index];
            }
        }

        [WebMethod]
        public void MarkSynchronized(string userName)
        {
            var updateLastSync = new StringBuilder("update [user] set LastSync = GetDate() where userName = '").Append(userName).Append("'").ToString();
            command.CommandText = updateLastSync;
            command.ExecuteNonQuery();
        }

        [WebMethod]
        public bool Login(string userName, string password)
        {
            return !userName.Equals("") && !password.Equals("");
        }

        [WebMethod]
        public DateTime CheckLastSync(string userName)
        {
            var lastSync = from c in _context.Users where c.UserName == userName select c.LastSync;
            var dateTime = lastSync.Single();

            if (dateTime != null)
            {
                return (DateTime)dateTime;
            }

            return new DateTime();
        }

        [WebMethod]
        public List<ArrayList> GetData(String userName, String tableName, String lastSyncTime)
        {
            var result = new List<ArrayList>();

            var userNameVar = from c in _context.Users where c.UserName == userName select c.ID;
            if (userNameVar.Count() != 1)
            {
                return result;
            }

            var userId = userNameVar.Single();



            conn.Open();

            var cmd = "select " + _tableMap[tableName] + " " +
                      "from dbo.[" + tableName + "]t " +
                      "where t.UserID = " + userId + " " +
                      "and t.ModifiedDate > CONVERT(datetime, '" + lastSyncTime + "') " +
                      "or t.LastSync > CONVERT(datetime, '" + lastSyncTime + "')";

            var dataAdapter = new SqlDataAdapter(cmd, conn);

            var dataSet = new DataSet();

            dataAdapter.Fill(dataSet, tableName);

            var dataTable = dataSet.Tables[tableName];

            foreach (DataRow row in dataTable.Rows)
            {
                var subResult = new ArrayList();

                foreach (DataColumn col in dataTable.Columns)
                {
                    subResult.Add(col.DataType == typeof (DateTime)
                                      ? Convert.ToDateTime(row[col]).ToString("yyyy-MM-dd HH:mm:ss")
                                      : row[col].ToString());
                }

                result.Add(subResult);
            }

            conn.Close();

            return result;
        }

        [WebMethod]
        public bool SaveData(String userName, String tableName, List<ArrayList> data)
        {
            if (data.Count != 0)
            {
                var userNameVar = from c in _context.Users where c.UserName == userName select c.ID;
                if (userNameVar.Count() != 1)
                {
                    return false;
                }

                var userId = userNameVar.Single();
              
                conn.Open();

                foreach (var recordRow in data)
                {
                    var clientModifiedDate = Convert.ToDateTime(recordRow[2]);

                    var resultGetData = recordRow.Cast<string>().Aggregate("", (current, subData) => current + ("'" + subData + "', "));

                    var sqlCheckCount = "select ModifiedDate from " + tableName + " where UserID = '" + userId +
                                        "' and id= '" + recordRow[0] + "'";
                    
                    command.CommandText = sqlCheckCount;
                    var countRecord = command.ExecuteReader();

                    if (!countRecord.HasRows)
                    {
                        var sqlCommand = "Insert into " + tableName + "(" + _tableMap[tableName] + ", LastSync, UserID) values (" + resultGetData + "GETDATE(), " + userId + ")";
                        countRecord.Close();
                        try
                        {
                            command.CommandText = sqlCommand;
                            command.ExecuteNonQuery();
                        }
                        catch (Exception)
                        {
                            return false;
                        }
                    }
                    else
                    {
                        countRecord.Read();
                        var serverModifiedDate = countRecord["ModifiedDate"];
                        if (clientModifiedDate.CompareTo(Convert.ToDateTime(serverModifiedDate.ToString())) > 0)
                        {
                            var subSColumns = _tableMap[tableName].Split(',');
                            var subResultGetData = resultGetData.Split(',');

                            var sqlCommand = "Update " + tableName + " set ";

                            for (var j = 1; j < subSColumns.Length; j++)
                            {
                                sqlCommand += subSColumns[j] + " = " + subResultGetData[j] + ",";
                            }

                            sqlCommand += " LastSync = GETDATE() where ID = " + recordRow[0] + " and UserID = " + userId;

                            try
                            {
                                command.CommandText = sqlCommand;
                                command.ExecuteNonQuery();
                            }
                            catch (Exception)
                            {
                                return false;
                            }
                        }
                    }

                    countRecord.Close();
                }

                conn.Close();

                return true;
            }

            return false;
        }
    }
}