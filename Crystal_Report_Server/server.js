// server.js
const express = require('express');
const bodyParser = require('body-parser');
const jasper = require('node-jasper-report'); 
const db = require('./src/firebase'); 

const app = express();
app.use(bodyParser.json());

// Path configurations
const jasperOptions = {
  reports: './reports', // Path to your .jasper or .jrxml files
  out: './out',         // Output path for generated reports
};

// Route to generate the report
app.post('/generate-report', async (req, res) => {
  try {
    const { reportName, userId } = req.body;

    // Fetch user data from Firebase Realtime Database
    const userRef = db.ref('users/' + userId);
    const userSnapshot = await userRef.once('value');
    const userData = userSnapshot.val();

    if (!userData) {
      return res.status(404).send({ message: 'User not found' });
    }

    // Report data that will be applied
    const reportData = {
      report: `${jasperOptions.reports}/${reportName}.jasper`, // Path to report file
      data: {
        name: userData.name,
        email: userData.email,
      },
    };

    // Generate the report in PDF format
    const pdfBuffer = jasper.pdf(reportData);

    // Write the PDF file to the output directory
    const outputFilePath = `${jasperOptions.out}/${userId}.pdf`;
    fs.writeFileSync(outputFilePath, pdfBuffer);

    // Send the generated PDF report as a response
    res.download(outputFilePath);

  } catch (error) {
    console.error('Error generating report:', error);
    res.status(500).send({ message: 'Report generation failed' });
  }
});



// Start the server
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});

