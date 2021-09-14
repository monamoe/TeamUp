/* eslint-disable max-len */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();
const db = admin.firestore();

exports.eventExpired = functions.pubsub.schedule("0 0 * * *").timeZone("America/Toronto").onRun(async (context) => {
  const x = await db.collection("Location");
  x.get().then(function(querySnapshot) {
    querySnapshot.forEach(async function(doc) {
      const y = await db.collection("Location/" + doc.id + "/Events")
          .where("endDate", "<", new Date());
      console.log("Inside location " + doc.id);

      y.get().then(function(q) {
        q.forEach(async function(e) {
          console.log("Inside events " + e.id);
          await db.collection("Location/" + doc.id + "/Events").doc(e.id).delete();
        });
      });
    });
  });
  return console.log("Bob says hi");
});
