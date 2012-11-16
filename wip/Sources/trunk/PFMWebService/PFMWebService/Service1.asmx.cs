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
    /// Summary description for Service1
    /// </summary>
    [WebService(Namespace = "http://tempuri.org/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class Service1 : System.Web.Services.WebService
    {
        readonly PFMDataClassesDataContext _context = new PFMDataClassesDataContext();

        [WebMethod]
        public string HelloWorld()
        {
            return "Hello World";
        }

        [WebMethod]
        public bool Login(String userName, String password)
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

        /// <summary>
        /// This method is used to save data from client.
        /// </summary>
        /// <param name="userName">User name account of client machine.</param>
        /// <param name="tableName">The table needs to be updated.</param>
        /// <param name="data">The specified data that should be stored.</param>
        [WebMethod]
        public void SaveData(string userName, string tableName, List<ArrayList> data)
        {
        }

        [WebMethod]
        public List<ArrayList> GetData(string userName, string tableName, string lastSyncTime)
        {
            var result = new List<ArrayList>();

            string[] sTables = { "Schedule", "ScheduleDetail", "EntryDetail", "Entry", "BorrowLend", "Category" };
            string[] sColumns = {"Id, CreatedDate, ModifiedDate, Budget, Type, IsDelete, StartDate, EndDate",
                                 "Id, CreatedDate, ModifiedDate, Budget, IsDelete, CategoryID, ScheduleID",
                                 "Id, CreatedDate, ModifiedDate, CategoryID, Name, CreatedDate, ModifiedDate, IsDelete, Money, EntryID",
                                 "Id, CreatedDate, ModifiedDate, IsDelete, Date, Type",
                                 "Id, CreatedDate, ModifiedDate, IsDelete, DebtType, Money, InterestType, InterestRate, StartDate, ExpiredDate, PersonName, PersonPhone, PersonAddress",
                                 "Id, CreatedDate, ModifiedDate, Name, IsDelete, UserColor"};

            var position = 0;

            for (var i = 0; i < sTables.Length; i++)
            {
                if (tableName.Equals(sTables[i]))
                    position = i;
            }

            var userNameVar = from c in _context.Users where c.UserName == userName select c.ID;
            if (!userNameVar.Any())
            {
                return null;
            }

            var userId = userNameVar.Single();

            const string connectionString = "Data Source=localhost;Initial Catalog=PFMDatabase;Integrated Security=True";

            var conn = new SqlConnection(connectionString);

            conn.Open();

            var cmd = "select " + sColumns[position] + " " +
                      "from dbo.[" + tableName + "]t " +
                      "where t.UserID = " + userId + " " +
                      "and t.ModifiedDate > CONVERT(datetime, '" + lastSyncTime + "')";

            var dataAdapter = new SqlDataAdapter(cmd, conn);

            var dataSet = new DataSet();

            dataAdapter.Fill(dataSet, tableName);

            var dataTable = dataSet.Tables[tableName];

            foreach (DataRow row in dataTable.Rows)
            {
                var subResult = new ArrayList();

                foreach (DataColumn col in dataTable.Columns)
                {
                    subResult.Add(row[col].ToString());
                }

                result.Add(subResult);
            }

            conn.Close();

            return result;
        }

        [WebMethod]
        public bool UpdateDataCategory(List<Category> cate)
        {

            return true;
        }
    }
}