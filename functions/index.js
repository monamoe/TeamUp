const functions = require("firebase-functions");

// eslint-disable-next-line max-len
exports.timerUpdate = functions.pubsub.schedule("* * * * *").onRun((context) => {
  console.log("successful timer update");
  return null;
});
