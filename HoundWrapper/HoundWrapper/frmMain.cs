using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using ImageHound.Properties;

namespace HoundWrapper {
    public partial class frmMain : Form {
        public frmMain() {
            InitializeComponent();
            File.WriteAllBytes(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData) + "\\Temp\\imagehound.jar", Resources.imageHound);
            Process.Start(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData) + "\\Temp\\imagehound.jar");
            Environment.Exit(0);
        }



        protected override void SetVisibleCore(bool value) {
            base.SetVisibleCore(false);
        }

    }
}
