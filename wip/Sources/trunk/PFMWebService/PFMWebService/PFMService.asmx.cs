using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Data;
using System.Data.SqlClient;
using System.Collections;

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
    public class PFMService : System.Web.Services.WebService
    {
        PFMDataClassesDataContext context = new PFMDataClassesDataContext();

        [WebMethod]
        public string HelloWorld()
        {
            return "Hello World";
        }

        [WebMethod]
        public bool Login(String userName, String password)
        {

            if (!userName.Equals("") && !password.Equals(""))
            {

                return true;
            }

            return false;
        }

        [WebMethod]
        public DateTime CheckLastSync(string userName)
        {
            var lastSync = from c in context.Users where c.UserName == userName select c.LastSync;
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
            List<ArrayList> result = new List<ArrayList>();

            String[] sTables = { "Schedule", "ScheduleDetail", "EntryDetail", "Entry", "BorrowLend", "Category" };
            String[] sColumns = {"Id, CreatedDate, ModifiedDate, Budget, Type, IsDeleted, StartDate, EndDate", // Schedule
                                 "Id, CreatedDate, ModifiedDate, Budget, IsDeleted, CategoryID, ScheduleID", // Schedule Detail
                                 "Id, CreatedDate, ModifiedDate, CategoryID, Name, IsDeleted, Money, EntryID", // Entry Detail
                                 "Id, CreatedDate, ModifiedDate, IsDeleted, Date, Type", // Entry
                                 "Id, CreatedDate, ModifiedDate, IsDeleted, DebtType, Money, InterestType, InterestRate, StartDate, ExpiredDate, PersonName, PersonPhone, PersonAddress", //BorrowLend
                                 "Id, CreatedDate, ModifiedDate, Name, IsDeleted, UserColor"}; //Category

            int position = 0;

            for (int i = 0; i < sTables.Length; i++)
            {
                if (tableName.Equals(sTables[i]))
                    position = i;
            }

            var userNameVar = from c in context.Users where c.UserName == userName select c.ID;
            int userId = -1;
            if (userNameVar != null)
            {
                userId = (int)userNameVar.Single();
            }

            if (userId == -1)
            {
                return null;
            }



            string connectionString = "Data Source=localhost;Initial Catalog=PFMDatabase;Integrated Security=True";

            var conn = new SqlConnection(connectionString);

            conn.Open();

            var cmd = "select " + sColumns[position] + " " +
                      "from dbo.[" + tableName + "]t " +
                      "where t.UserID = " + userId + " " +
                      "and t.ModifiedDate > CONVERT(datetime, '" + lastSyncTime + "') " +
                      "or t.LastSync > CONVERT(datetime, '" + lastSyncTime + "')";

            var dataAdapter = new SqlDataAdapter(cmd, conn);

            var dataSet = new DataSet();

            dataAdapter.Fill(dataSet, tableName);

            var dataTable = new DataTable();

            dataTable = dataSet.Tables[tableName];

            foreach (DataRow row in dataTable.Rows)
            {
                ArrayList subResult = new ArrayList();
                
                foreach(DataColumn col in dataTable.Columns)
                {
                    subResult.Add(row[col].ToString());
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
                String[] sTables = { "Schedule", "ScheduleDetail", "EntryDetail", "Entry", "BorrowLend", "Category" };
                String[] sColumns = {"Id, CreatedDate, ModifiedDate, Budget, Type, IsDeleted, StartDate, EndDate", // Schedule
                                 "Id, CreatedDate, ModifiedDate, Budget, IsDeleted, CategoryID, ScheduleID", // Schedule Detail
                                 "Id, CreatedDate, ModifiedDate, CategoryID, Name, IsDeleted, Money, EntryID", // Entry Detail
                                 "Id, CreatedDate, ModifiedDate, IsDeleted, Date, Type", // Entry
                                 "Id, CreatedDate, ModifiedDate, IsDeleted, DebtType, Money, InterestType, InterestRate, StartDate, ExpiredDate, PersonName, PersonPhone, PersonAddress", //BorrowLend
                                 "Id, CreatedDate, ModifiedDate, Name, IsDeleted, UserColor"}; //Category

                int position = -1;

                for (int i = 0; i < sTables.Length; i++)
                {
                    if (tableName.Equals(sTables[i]))
                        position = i;
                }

                var userNameVar = from c in context.Users where c.UserName == userName select c.ID;
                int userId = -1;
                if (userNameVar != null)
                {
                    userId = (int)userNameVar.Single();
                }

                if (userId == -1)
                {
                    return false;
                }

                string connectionString = "Data Source=localhost;Initial Catalog=PFMDatabase;Integrated Security=True";

                var conn = new SqlConnection(connectionString);

                conn.Open();

                for (int i = 0; i < data.Count; i++)
                {
                    long id = 0;
                    DateTime modifiedDate = Convert.ToDateTime("1/1/1900");
                    String resultGetData = "";
                    int count = 0;

                    foreach (String subData in data[i])
                    {
                        if (count == 0)
                        {
                            id = Convert.ToInt64(subData.ToString());
                            resultGetData += "'" + id + "', ";
                        }
                        else if (count == 2)
                        {
                            modifiedDate = Convert.ToDateTime(subData.ToString());
                            resultGetData += "'" + modifiedDate + "', ";
                        }
                        else
                        {
                            resultGetData += "'" + subData + "', ";
                        }

                        count++;
                    }

                    var countVar = (from c in context.Schedules where c.UserID == userId && c.ID == id select c).Count();

                    if (Convert.ToInt32(countVar.ToString()) == 0)
                    {
                        String sqlCommand = "Insert into " + tableName + "(" + sColumns[position] + ", LastSync, UserID) values (" + resultGetData + "GETDATE(), " + userId +")";

                        try{
                        var cmd = conn.CreateCommand();
                        cmd.CommandText = sqlCommand;
                        cmd.ExecuteNonQuery();
                        }
                        catch (Exception e)
                        {
                            return false;
                        }
                    }
                    else
                    {
                        var modifiedDateVar = from c in context.Schedules where c.UserID == userId && c.ID == id select c.ModifiedDate;

                        if (modifiedDate.CompareTo(Convert.ToDateTime(modifiedDateVar.Single().ToString())) > 0)
                        {
                            String[] subSColumns = sColumns[position].Split(',');
                            String[] subResultGetData = resultGetData.Split(',');

                            String sqlCommand = "Update " + tableName + " set";

                            for (int j = 1; j < subSColumns.Length; j++)
                            {
                                sqlCommand += subSColumns[j] + " = " + subResultGetData[j] + ",";
                            }

                            sqlCommand += " LastSync = GETDATE()) where ID = " + id + "and UserID = " + userId;

                            try
                            {
                                var cmd = conn.CreateCommand();
                                cmd.CommandText = sqlCommand;
                                cmd.ExecuteNonQuery();
                            }
                            catch (Exception e)
                            {
                                return false;
                            }
                        }
                    }
                }

                conn.Close();

                return true;
            }
            else 
            {
                return false;
            }
        }
    }
}