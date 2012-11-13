using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using VLocationApplication.VLocationServiceReference;

namespace VLocationApplication
{
    public partial class FormMain : Form
    {
        VLocationServiceReference.Service1SoapClient service = new VLocationServiceReference.Service1SoapClient();

        public FormMain()
        {
            InitializeComponent();
        }

        private void btnHelloWorld_Click(object sender, EventArgs e)
        {
            MessageBox.Show(service.HelloWorld());
        }

        private void btnGetCategory_Click(object sender, EventArgs e)
        {
            int id = Int16.Parse(txtID.Text);
            VLocationServiceReference.Category category = service.GetCategory(id);

            if (category != null)
                MessageBox.Show(String.Format("Category: Name:[{0}]    CreatedDate: [{1}]", category.Name.Trim(), category.CreatedDate));
        }

        private void btnInsertCategory_Click(object sender, EventArgs e)
        {
            String error = service.InsertCategory(txtName.Text);
            if (error != null)
                MessageBox.Show(String.Format("Insert fail with error: {0}", error));
            else
                MessageBox.Show("Insert successfully!");
        }

        private void btnGetCategories_Click(object sender, EventArgs e)
        {
            Category[] categories = service.GetCategories();

            StringBuilder categoryStr = new StringBuilder();
            foreach (Category category in categories)
            {
                categoryStr.Append(String.Format("Category {0}: Name: [{1}]    CreatedDate: [{2}]", category.ID, category.Name.Trim(), category.CreatedDate))
                    .Append(Environment.NewLine).Append(Environment.NewLine);
            }

            MessageBox.Show(categoryStr.ToString());
        }
    }
}
