/* eslint-disable max-len */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();
const db = admin.firestore();

exports.eventExpired = functions.pubsub.schedule("0 0 * * *").timeZone("America/Toronto").onRun(async (context) => {
  const x = await db.collection("Location");// get locations
  x.get().then(function(querySnapshot) {
    querySnapshot.forEach(async function(doc) {// loop through locations
      const expiredEvents = await db.collection("Location/" + doc.id + "/Events")
          .where("endDate", "<", new Date()); // gets events that have expired

      expiredEvents.get().then(function(expiredEvent) {
        expiredEvent.forEach(async function(e) {// loop through expired events
          const hosts = await db.collection("User/" + e.get("hostID") + "/Hosting").where("eventID", "==", e.id);
          hosts.get().then(function(query) {
            query.forEach(async function(host) {
              console.log(e.get("hostID") + " hi " + host.id);
              await db.collection("User/" + e.get("hostID") + "/Hosting").doc(host.id).delete(); // deletes event host
            });
          });

          const users = await db.collection("User");// gets users
          users.get().then(function(snap) {
            snap.forEach(async function(docu) {// loops through users
              const attending = await db.collection("User/" + docu.id + "/Attending").where("eventID", "==", e.id);
              attending.get().then(function(query) {
                query.forEach(async function(attend) {
                  await db.collection("User/" + docu.id + "/Attending").doc(attend.id).delete(); // deletes event attendies
                });
              });
            });
          });

          const attendees = await db.collection("Location/" + doc.id + "/Events/"+e.id+"/Attendees");
          attendees.get().then(function(query) {
            query.forEach(async function(a) {
              await db.collection("Location/" + doc.id + "/Events/" + e.id + "/Attendees").doc(a.id).delete(); // deletes event attendes
            });
          });

          await db.collection("Location/" + doc.id + "/Events").doc(e.id).delete();// delete expired events
        });
      });
    });
  });
  return console.log("Bob says hi");
});
