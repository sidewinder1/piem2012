using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.Data;
using System.Data.SqlClient;

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
        public List<String> GetData(String username, String tableName, String lastSyncTime)
        {
            List<String> result = new List<String>();

            String[] sTables = { "Schedule", "ScheduleDetail", "EntryDetail", "Entry", "BorrowLend", "Category" };
            String[] sColumns = {"Id, Budget, Type, CreatedDate, ModifiedDate, IsDeleted, Start_date, End_date",
                                 "Id, Budget, CreatedDate, ModifiedDate, IsDeleted, Category_Id, Schedule_Id",
                                 "Id, Category_Id, Name, CreatedDate, ModifiedDate, IsDeleted,Money, Entry_Id",
                                 "Id, CreatedDate, ModifiedDate, IsDeleted,Date, Type",
                                 "ID, CreatedDate, ModifiedDate, IsDeleted, Debt_type, Money, Interest_type, Interest_rate, Start_date, Expired_date, Person_name, Person_phone, Person_address",
                                 "Id,Name, CreatedDate, ModifiedDate, IsDeleted,User_Color"};

            int position = 0;

            for(int i=0; i<sTables.Length; i++)
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



            const string connectionString = "Data Source=localhost;Initial Catalog=PFMDatabase;Integrated Security=True";
            using (var sqlConnection = new SqlConnection(connectionString))
            {
                String sqlCommand = "select " + sColumns[position] +
                                    "from dbo.Category t " +
                                    "where t.UserID = " + userId + " " +
                                    "and t.ModifiedDate > CONVERT(datetime, '" + lastSyncTime + "')";


                var table = new SqlCommand(sqlCommand, sqlConnection);

                var adapterTable = new SqlDataAdapter(table);
                var ds = new DataSet();

                adapterTable.Fill(ds, tableName);

            }

            

            return result;
        }

        [WebMethod]
        public bool UpdateDataCategory(List<Category> cate)
        {


            return true;
        }
    }
}