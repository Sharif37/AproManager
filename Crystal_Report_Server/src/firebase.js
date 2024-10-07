// firebase.js
var admin = require("firebase-admin");

var serviceAccount = require("./../apromanager-972c5-firebase-adminsdk-pu0im-163873d30f.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://apromanager-972c5-default-rtdb.asia-southeast1.firebasedatabase.app"
});

const db = admin.database();
module.exports = db;
