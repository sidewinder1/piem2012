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
            var username = from c in context.Users where c.UserName == userName select c;

            /*
            if (username.ToList() == null)
                return false;
            else
                return true;
            */

            if (!userName.Equals("") && !password.Equals(""))
                return true;
            else
                return false;
        }

        [WebMethod]
        public DateTime CheckLastSync(int UserID)
        {
            var lastSync = from c in context.Users where c.ID == UserID select c.LastSync;
            return (DateTime)lastSync.Single();
        }

        [WebMethod]
        public DataSet GetData(String tableName)
        {
            
            const string connectionString = "Data Source=TUANNA01030-PC;Initial Catalog=PFMDatabase;Integrated Security=True"; 
            using (var sqlConnection = new SqlConnection(connectionString)) 
            { 
                var table = new SqlCommand("SELECT * FROM dbo." + tableName, sqlConnection);

                var adapterTable = new SqlDataAdapter(table);
                var ds = new DataSet(); 

                adapterTable.Fill(ds, tableName);

                return ds; 
            }
            
            /*
            DataSet ds = new DataSet();

            if (tableName.Equals("BorrowLend"))
            {
                var dataTable = from c in context.BorrowingLendings select c;

            }
            */
        }
    }
}