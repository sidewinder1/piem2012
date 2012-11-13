using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;

namespace WebServiceSample
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
        VLocationDataClassesDataContext context = new VLocationDataClassesDataContext();

        [WebMethod]
        public string HelloWorld()
        {
            return "Hello World";
        }

        [WebMethod]
        public Category GetCategory(int id)
        {
            var category = from c in context.Categories
                           where c.ID == id
                           select c;

            return category.Single();
        }

        [WebMethod]
        public List<Category> GetCategories()
        {
            var category = from c in context.Categories
                           select c;

            return category.ToList();
        }

        [WebMethod]
        public String InsertCategory(String name)
        {
            Category category = new Category();
            category.Name = name;
            category.CreatedDate = DateTime.Now;

            try
            {
                context.Categories.InsertOnSubmit(category);
                context.SubmitChanges();
                return null;
            }
            catch (Exception ex)
            {
                return ex.ToString();
            }

        }
    }
}