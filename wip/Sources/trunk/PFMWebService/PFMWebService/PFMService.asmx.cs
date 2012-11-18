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

            if (tableName.Equals("Schedule"))
            {
                // List<PFMWebServiceModel.Schedule> scheduleList = new List<PFMWebServiceModel.Schedule>();

                for (int i = 0; i < data.Count; i++)
                {
                    foreach (ArrayList subData in data[i])
                    {
                        long id = Convert.ToInt64(subData[0].ToString());
                        DateTime createdDate = Convert.ToDateTime(subData[1].ToString());
                        DateTime modifiedDate = Convert.ToDateTime(subData[2].ToString());
                        double budget = Convert.ToDouble(subData[3].ToString());
                        int type = Convert.ToInt32(subData[4].ToString());
                        int isDeleted = Convert.ToInt32(subData[5].ToString());
                        DateTime startDate = Convert.ToDateTime(subData[6].ToString());
                        DateTime endDate = Convert.ToDateTime(subData[7].ToString());

                        var modifiedDateVar = from c in context.Schedules where c.UserID == userId && c.ID == id select c.ModifiedDate;

                        if (modifiedDateVar == null)
                        {
                            String sqlCommand = "Insert into Schedule values (" + id + ", " + userId + ", " + budget + ", " + type + ", " + startDate + ", " + endDate + ", " + isDeleted + ", " + createdDate + ", " + modifiedDate + ", GETDATE())";

                            var cmd = conn.CreateCommand();
                            cmd.CommandText = sqlCommand;
                            cmd.ExecuteNonQuery();
                        }
                        else 
                        {
                            if (modifiedDate.CompareTo((DateTime)modifiedDateVar.Single()) > 0)
                            {
                                String sqlCommand = "Update Schedule set Budget = " + budget + ", Type = " + type + ", StartDate = " + startDate + ", EndDate = " + endDate + ", IsDeleted = " + isDeleted + ", CreatedDate = " + createdDate + ", ModifiedDate = " + modifiedDate + ", LastSync = GETDATE()) where ID = " + id + "and UserID = " + userId;

                                var cmd = conn.CreateCommand();
                                cmd.CommandText = sqlCommand;
                                cmd.ExecuteNonQuery();    
                            }
                        }

                        // PFMWebServiceModel.Schedule scheduleData = new PFMWebServiceModel.Schedule(id, createdDate, modifiedDate, budget, type, isDeleted, startDate, endDate);

                        // scheduleList.Add(scheduleData);
                    }
                }


            }
            else if (tableName.Equals("ScheduleDetail"))
            {
                for (int i = 0; i < data.Count; i++)
                {
                    foreach (ArrayList subData in data[i])
                    {
                        long id = Convert.ToInt64(subData[0].ToString());
                        DateTime createdDate = Convert.ToDateTime(subData[1].ToString());
                        DateTime modifiedDate = Convert.ToDateTime(subData[2].ToString());
                        double budget = Convert.ToDouble(subData[3].ToString());
                        int isDeleted = Convert.ToInt32(subData[5].ToString());
                        int categoryID = Convert.ToInt32(subData[6].ToString());
                        int scheduleID = Convert.ToInt32(subData[7].ToString());

                        var modifiedDateVar = from c in context.ScheduleDetails where c.UserID == userId && c.ID == id select c.ModifiedDate;

                        if (modifiedDateVar == null)
                        {
                            String sqlCommand = "Insert into ScheduleDetail values (" + id + ", " + userId + ", " + budget + ", " + categoryID + ", " + scheduleID + ", " + isDeleted + ", " + createdDate + ", " + modifiedDate + ", GETDATE())";

                            var cmd = conn.CreateCommand();
                            cmd.CommandText = sqlCommand;
                            cmd.ExecuteNonQuery();
                        }
                        else
                        {
                            if (modifiedDate.CompareTo((DateTime)modifiedDateVar.Single()) > 0)
                            {
                                String sqlCommand = "Update ScheduleDetail set Budget = " + budget + ", CategoryID = " + categoryID + ", ScheduleID = " + scheduleID + ", IsDeleted = " + isDeleted + ", CreatedDate = " + createdDate + ", ModifiedDate = " + modifiedDate + ", LastSync = GETDATE()) where ID = " + id + "and UserID = " + userId;

                                var cmd = conn.CreateCommand();
                                cmd.CommandText = sqlCommand;
                                cmd.ExecuteNonQuery();
                            }
                        }
                    }
                }
            }
            else if (tableName.Equals("EntryDetail"))
            {
                for (int i = 0; i < data.Count; i++)
                {
                    foreach (ArrayList subData in data[i])
                    {
                        long id = Convert.ToInt64(subData[0].ToString());
                        DateTime createdDate = Convert.ToDateTime(subData[1].ToString());
                        DateTime modifiedDate = Convert.ToDateTime(subData[2].ToString());
                        int categoryID = Convert.ToInt32(subData[3].ToString());
                        String name = subData[4].ToString();
                        int isDeleted = Convert.ToInt32(subData[5].ToString());
                        double money = Convert.ToDouble(subData[6].ToString());
                        int entryID = Convert.ToInt32(subData[7].ToString());

                        var modifiedDateVar = from c in context.EntryDetails where c.UserID == userId && c.ID == id select c.ModifiedDate;

                        if (modifiedDateVar == null)
                        {
                            String sqlCommand = "Insert into EntryDetail values (" + id + ", " + userId + ", " + categoryID + ", " + name + ", " + money + ", " + entryID + ", " + isDeleted + ", " + createdDate + ", " + modifiedDate + ", GETDATE())";

                            var cmd = conn.CreateCommand();
                            cmd.CommandText = sqlCommand;
                            cmd.ExecuteNonQuery();
                        }
                        else
                        {
                            if (modifiedDate.CompareTo((DateTime)modifiedDateVar.Single()) > 0)
                            {
                                String sqlCommand = "Update EntryDetail set CategoryID = " + categoryID + ", Name = " + name + ", [Money] = " + money + "EntryID = " + entryID + ", IsDeleted = " + isDeleted + ", CreatedDate = " + createdDate + ", ModifiedDate = " + modifiedDate + ", LastSync = GETDATE()) where ID = " + id + "and UserID = " + userId;

                                var cmd = conn.CreateCommand();
                                cmd.CommandText = sqlCommand;
                                cmd.ExecuteNonQuery();
                            }
                        }
                    }
                }
            }
            else if (tableName.Equals("Entry"))
            {
                for (int i = 0; i < data.Count; i++)
                {
                    foreach (ArrayList subData in data[i])
                    {
                        long id = Convert.ToInt64(subData[0].ToString());
                        DateTime createdDate = Convert.ToDateTime(subData[1].ToString());
                        DateTime modifiedDate = Convert.ToDateTime(subData[2].ToString());
                        int isDeleted = Convert.ToInt32(subData[3].ToString());
                        DateTime date = Convert.ToDateTime(subData[4].ToString());
                        int type = Convert.ToInt32(subData[5].ToString());

                        var modifiedDateVar = from c in context.EntryDetails where c.UserID == userId && c.ID == id select c.ModifiedDate;

                        if (modifiedDateVar == null)
                        {
                            String sqlCommand = "Insert into Entry values (" + id + ", " + userId + ", " + date + ", " + isDeleted + ", " + type + "," + createdDate + ", " + modifiedDate + ", GETDATE())";

                            var cmd = conn.CreateCommand();
                            cmd.CommandText = sqlCommand;
                            cmd.ExecuteNonQuery();
                        }
                        else
                        {
                            if (modifiedDate.CompareTo((DateTime)modifiedDateVar.Single()) > 0)
                            {
                                String sqlCommand = "Update Entry set [Date] = " + date + ", IsDeleted = " + isDeleted + ", [Type] = " + type + ", CreatedDate = " + createdDate + ", ModifiedDate = " + modifiedDate + ", LastSync = GETDATE()) where ID = " + id + "and UserID = " + userId;

                                var cmd = conn.CreateCommand();
                                cmd.CommandText = sqlCommand;
                                cmd.ExecuteNonQuery();
                            }
                        }
                    }
                }
            }
            else if (tableName.Equals("BorrowLend"))
            {
                for (int i = 0; i < data.Count; i++)
                {
                    foreach (ArrayList subData in data[i])
                    {
                        long id = Convert.ToInt64(subData[0].ToString());
                        DateTime createdDate = Convert.ToDateTime(subData[1].ToString());
                        DateTime modifiedDate = Convert.ToDateTime(subData[2].ToString());
                        int isDeleted = Convert.ToInt32(subData[3].ToString());
                        String debtType = subData[4].ToString();
                        double money = Convert.ToDouble(subData[5].ToString());
                        String interestType = subData[6].ToString();
                        int interestRate = Convert.ToInt32(subData[7].ToString());
                        DateTime startDate = Convert.ToDateTime(subData[8].ToString());
                        DateTime expiredDate = Convert.ToDateTime(subData[9].ToString());
                        String personName = subData[10].ToString();
                        String personPhone = subData[11].ToString();
                        String personAddress = subData[12].ToString();

                        var modifiedDateVar = from c in context.BorrowLends where c.UserID == userId && c.ID == id select c.ModifiedDate;

                        if (modifiedDateVar == null)
                        {
                            String sqlCommand = "Insert into BorrowLend values (" + id + ", " + userId + ", " + debtType + money + ", " + interestType + ", " + interestRate + ", " + startDate + ", " + expiredDate + ", " + personName + ", " + personPhone + ", " + personAddress + ", " + isDeleted + createdDate + ", " + modifiedDate + ", GETDATE())";

                            var cmd = conn.CreateCommand();
                            cmd.CommandText = sqlCommand;
                            cmd.ExecuteNonQuery();
                        }
                        else
                        {
                            if (modifiedDate.CompareTo((DateTime)modifiedDateVar.Single()) > 0)
                            {
                                String sqlCommand = "Update BorrowLend set DebtType = " + debtType + 
                                                    ", [Money] = " + money + 
                                                    ", InterestType = " + interestType + 
                                                    ", InterestRate = " + interestRate + 
                                                    ", StartDate = " + startDate + 
                                                    ", ExpiredDate = " + expiredDate + 
                                                    ", PersonName = " + personName + 
                                                    ", PersonPhone = " + personPhone + 
                                                    ", PersonAddress = " + personAddress + 
                                                    ", IsDeleted = " + isDeleted + 
                                                    ", CreatedDate = " + createdDate + 
                                                    ", ModifiedDate = " + modifiedDate + 
                                                    ", LastSync = GETDATE()) where ID = " + id + "and UserID = " + userId;

                                var cmd = conn.CreateCommand();
                                cmd.CommandText = sqlCommand;
                                cmd.ExecuteNonQuery();
                            }
                        }
                    }
                }
            }
            else if (tableName.Equals("Category"))
            {
                for (int i = 0; i < data.Count; i++)
                {
                    foreach (ArrayList subData in data[i])
                    {
                        long id = Convert.ToInt64(subData[0].ToString());
                        DateTime createdDate = Convert.ToDateTime(subData[1].ToString());
                        DateTime modifiedDate = Convert.ToDateTime(subData[2].ToString());
                        String name = subData[3].ToString();
                        int isDeleted = Convert.ToInt32(subData[4].ToString());
                        String userColor = subData[5].ToString();

                        var modifiedDateVar = from c in context.Categories where c.UserID == userId && c.ID == id select c.ModifiedDate;

                        if (modifiedDateVar == null)
                        {
                            String sqlCommand = "Insert into Category values (" + id + ", " + userId + ", " + name + ", " + userColor + ", " + isDeleted + createdDate + ", " + modifiedDate + ", GETDATE())";

                            var cmd = conn.CreateCommand();
                            cmd.CommandText = sqlCommand;
                            cmd.ExecuteNonQuery();
                        }
                        else
                        {
                            if (modifiedDate.CompareTo((DateTime)modifiedDateVar.Single()) > 0)
                            {
                                String sqlCommand = "Update Category set Name = " + name +
                                                    ", UserColor = " + userColor +
                                                    ", IsDeleted = " + isDeleted +
                                                    ", CreatedDate = " + createdDate +
                                                    ", ModifiedDate = " + modifiedDate +
                                                    ", LastSync = GETDATE()) where ID = " + id + "and UserID = " + userId;

                                var cmd = conn.CreateCommand();
                                cmd.CommandText = sqlCommand;
                                cmd.ExecuteNonQuery();
                            }
                        }
                    }
                }
            }
            else
            {
                return false;
            }

            conn.Close();

            return true;
        }
    }
}