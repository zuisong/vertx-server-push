import { Client } from "https://esm.sh/@stomp/stompjs@7";
import cash from "https://esm.sh/cash-dom@8.1.4";
const $ = cash;
function onPageReady() {
  let stompClient;
  const host = window.location.host;
  const stompConfig = {
    // Typically login, passcode and vhost
    // Adjust these for your broker
    connectHeaders: {},
    // Broker URL, should start with ws:// or wss:// - adjust for your broker setup
    brokerURL: `ws://${host}/stomp`,
    // Keep it off for production, it can be quit verbose
    // Skip this key to disable
    debug: function(str) {
      console.log("STOMP: " + str);
    },
    // If disconnected, it will retry after 200ms
    reconnectDelay: 200,
    // Subscriptions should be done inside onConnect as those need to reinstated when the broker reconnects
    onConnect: function(frame) {
      const subscription = stompClient.subscribe("/topic/chat", (message) => {
        const payload = JSON.parse(message.body);
        displayIncomingMessage(payload.user, payload.message);
      });
    }
  };
  stompClient = new Client(stompConfig);
  stompClient.activate();
  $("#username").val("User " + Math.round(Math.random() * 100));
  function sendMessage() {
    if (publishMessage($("#username").val(), $("#usermsg").val())) {
      clearMessageInput();
    }
  }
  $("#submitmsg").on("click", sendMessage);
  function clearMessageInput() {
    $("#usermsg").val("");
  }
  function publishMessage(user, message) {
    if (!stompClient.connected) {
      alert("Broker disconnected, can't send message.");
      return false;
    }
    if (message.length > 0) {
      const payLoad = { user, message };
      fetch(
        `http://${host}/push?topic=/topic/chat`,
        {
          method: "post",
          headers: {
            "content-type": "application/json"
          },
          body: JSON.stringify(payLoad)
        }
      ).then((_) => {
      }, (err) => console.error(err));
    }
    return true;
  }
  function displayIncomingMessage(user, message) {
    const msgDiv = $("<div>").addClass("msgln");
    msgDiv.html(`<span class="user">[${user}]: </span><span class="message">${message}</span>`);
    $("#chatbox").append(msgDiv);
  }
}
$(onPageReady);
