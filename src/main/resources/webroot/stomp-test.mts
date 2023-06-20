// @ts-ignore
import {
  Client,
  IFrame,
  IMessage,
  StompConfig,
} from "https://esm.sh/@stomp/stompjs@7";
// @ts-ignore
import $ from "https://esm.sh/cash-dom@8.1.4";

function onPageReady() {
  let stompClient: Client;
  const host = window.location.host;
  const stompConfig: StompConfig = {
    // Typically login, passcode and vhost
    // Adjust these for your broker
    connectHeaders: {},

    // Broker URL, should start with ws:// or wss:// - adjust for your broker setup
    brokerURL: `ws://${host}/stomp`,

    // Keep it off for production, it can be quit verbose
    // Skip this key to disable
    debug: function (str: string) {
      console.log("STOMP: " + str);
    },

    // If disconnected, it will retry after 200ms
    reconnectDelay: 200,

    // Subscriptions should be done inside onConnect as those need to reinstated when the broker reconnects
    onConnect: function (frame: IFrame) {
      // The return object has a method called `unsubscribe`
      const subscription = stompClient.subscribe(
        "/topic/chat",
        (message: IMessage) => {
          const payload = JSON.parse(message.body);
          displayIncomingMessage(payload.user, payload.message);
        },
      );
    },
  };

  // Create an instance
  stompClient = new Client(stompConfig);

  // You can set additional configuration here

  // Attempt to connect
  stompClient.activate();

  // For the demo, set a random display user name for the chat, just feels nice
  $("#username").val("User " + Math.round(Math.random() * 100));

  function sendMessage() {
    if (
      publishMessage(
        $("#username").val() as string,
        $("#usermsg").val() as string,
      )
    ) {
      clearMessageInput();
    }
  }

  // Set the DOM event handlers must not be inside onConnect - other for each reconnect it will keep getting added
  $("#submitmsg").on("click", sendMessage);

  function clearMessageInput() {
    $("#usermsg").val("");
  }

  function publishMessage(user: string, message: string) {
    // trying to publish a message when the broker is not connected will throw an exception
    if (!stompClient.connected) {
      alert("Broker disconnected, can't send message.");
      return false;
    }
    if (message.length > 0) {
      const payLoad = { user: user, message: message };

      fetch(
        `http://${host}/push?topic=/topic/chat`,
        {
          method: "post",
          headers: {
            "content-type": "application/json",
          },
          body: JSON.stringify(payLoad),
        },
      )
        .then((_) => {}, (err) => console.error(err));
    }
    return true;
  }

  function displayIncomingMessage(user: string, message: string) {
    const msgDiv = $("<div>").addClass("msgln");
    msgDiv.html(
      `<span class="user">[${user}]: </span><span class="message">${message}</span>`,
    );
    $("#chatbox").append(msgDiv);
  }
}

$(onPageReady);
