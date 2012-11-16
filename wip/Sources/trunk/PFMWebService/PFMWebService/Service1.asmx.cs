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
        public DateTime CheckLastSync(string username)
        {
            var lastSync = from c in context.Users where c.UserName == username select c.LastSync;
            var dateTime = lastSync.Single();

            if (dateTime != null)
            {
                return (DateTime)dateTime;
            }

            return new DateTime();
        }

        [WebMethod]
        public List<ArrayList> GetData(String username, String tableName, String lastSyncTime)
        {
            List<ArrayList> result = new List<ArrayList>();

            String[] sTables = { "Schedule", "ScheduleDetail", "EntryDetail", "Entry", "BorrowLend", "Category" };
            String[] sColumns = {"Id, CreatedDate, ModifiedDate, Budget, Type, IsDelete, StartDate, EndDate",
                                 "Id, CreatedDate, ModifiedDate, Budget, IsDelete, CategoryID, ScheduleID",
                                 "Id, CreatedDate, ModifiedDate, CategoryID, Name, CreatedDate, ModifiedDate, IsDelete, Money, EntryID",
                                 "Id, CreatedDate, ModifiedDate, IsDelete, Date, Type",
                                 "Id, CreatedDate, ModifiedDate, IsDelete, DebtType, Money, InterestType, InterestRate, StartDate, ExpiredDate, PersonName, PersonPhone, PersonAddress",
                                 "Id, CreatedDate, ModifiedDate, Name, IsDelete, UserColor"};

            int position = 0;

            for (int i = 0; i < sTables.Length; i++)
            {
                if (tableName.Equals(sTables[i]))
                    position = i;
            }

            var userNameVar = from c in context.Users where c.UserName == username select c.ID;
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
                      "and t.ModifiedDate > CONVERT(datetime, '" + lastSyncTime + "')";

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
        public bool UpdateDataCategory(List<Category> cate)
        {


            return true;
        }
    }
}