const functions = require("firebase-functions");

// eslint-disable-next-line max-len
exports.eventExpired = functions.pubsub
  .schedule("0/5 * * * *")
  .timeZone("Canada/Toronto")
  .onRun((context) => {
    db.collection(Events)
      .document("endDate" < new Date())
      .delete()
  });
