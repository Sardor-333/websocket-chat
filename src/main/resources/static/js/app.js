const socket = new SockJS('/ws-endpoint');
var stompClient = Stomp.over(socket);

var currentUser = null;
var selectedContactId = null;

async function connect() {
    loader();
    console.log("=== INITIALIZED CURRENT USER AND CONTACTS ===")

    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        subscribeToMessageIncome();
        console.log("=== SUBSCRIBED TO MESSAGES ===");

        subscribeToContactActive();
        console.log("=== SUBSCRIBED TO CONTACTS ===");

        subscribeToTyping();
        console.log("=== SUBSCRIBED TO USER TYPING === ");

        subscribeToStopTyping();
        console.log("=== SUBSCRIBED TO USER STOPPED TYPING === ");
    });
}

async function loader() {
    await getCurrentUser();
    await getMyContacts();
}

function subscribeToMessageIncome() {
    stompClient.subscribe(
        '/user/' + currentUser.id + '/queue/messages',
        function (messageResponse) {
            const message = JSON.parse(messageResponse.body);

            if (selectedContactId !== null) {
                console.log(typeof message.sender.id);
                console.log(typeof selectedContactId);

                if (message.sender.id == selectedContactId || message.sender.id == currentUser.id) {
                    appendNewMessage(message);
                } else {
                    console.log('=== NOTIFICATION KELDI ===')
                    const oneContact = document.getElementById(message.sender.id.toString());
                    const notification = document.getElementById(message.sender.id.toString() + "badge");

                    if (notification != null) {
                        notification.innerText = (parseInt(notification.innerText, 10) + 1).toString();
                    } else {
                        const span = document.createElement('span');
                        span.classList.add(message.sender.id.toString() + "badge");
                        span.innerText = '1';
                        oneContact.appendChild(span);
                    }
                }
            }
        }
    );
}

function subscribeToContactActive() {
    stompClient.subscribe(
        '/user/' + currentUser.id + '/contacts',
        function (contactActivityResponse) {
            // CONTACT PROJECTION COME TO HERE
            const contact = JSON.parse(contactActivityResponse.body);
            console.log("=== CONTACT " + contact.id + " IS " + contact.online ? "ONLINE" : "OFFLINE");

            if (contact.online) {
                $("#" + contact.id + "")
                    .css("background-color", "green");
            } else {
                $("#" + contact.id + "")
                    .css("background-color", "yellow");
            }
        }
    );
}

function subscribeToTyping() {
    stompClient.subscribe(
        '/user/' + currentUser.id + '/typing',
        function (contactTyping) {
            if (contactTyping.body != null) {
                const typing = JSON.parse(contactTyping.body); // GET WHO IS TYPING TO ME ( SENDER )
                console.log("=== RECEIVER IS TYPING " + typing.id)
                const isTyping = document.getElementById(typing.id + "typing");
                if (isTyping == null) {
                    const div = document.getElementById(typing.id); // FIND SENDER AMONG MY RECEIVERS

                    const chatBubble = document.createElement('h4');
                    chatBubble.id = typing.id + "typing";
                    chatBubble.classList.add('chat-bubble');
                    div.appendChild(chatBubble);

                    const typingDiv = document.createElement('div');
                    typingDiv.classList.add('typing');
                    chatBubble.appendChild(typingDiv);

                    const dot1 = document.createElement('div');
                    dot1.classList.add('dot');
                    typingDiv.appendChild(dot1);

                    const dot2 = document.createElement('div');
                    dot2.classList.add('dot');
                    typingDiv.appendChild(dot2);

                    const dot3 = document.createElement('div');
                    dot3.classList.add('dot');
                    typingDiv.appendChild(dot3);
                }
            }
        }
    );
}

function subscribeToStopTyping() {
    stompClient.subscribe(
        '/user/' + currentUser.id + '/notTyping',
        function (contactNotTyping) {
            if (contactNotTyping.body != null) {
                const notTypingUser = JSON.parse(contactNotTyping.body); // GET WHO IS TYPING TO ME ( SENDER )
                console.log("=== RECEIVER HAS STOPPED TYPING " + notTypingUser.id)
                const typingContact = document.getElementById(notTypingUser.id + "typing");
                if (typingContact != null) {
                    document.getElementById(notTypingUser.id + "typing").remove();
                }
            }
        }
    );
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendMessage();
    });

});

var timeoutId = null;

function typing() {
    if (selectedContactId != null) {
        console.log("=== SELECTED CONTACT === " + selectedContactId);
        stompClient.send(
            "/app/chat/typing",
            {},
            JSON.stringify(
                {
                    'senderId': currentUser.id,
                    'receiverId': selectedContactId,
                    'typingText': $("#message").val()
                }
            )
        );

        if (timeoutId != null) {
            clearTimeout(timeoutId);
            console.log("=== BIRINCHI YOZMAYOTGANI UCHUN BOSHQATTAN 5 SEKUNDGA YOQILDI ===")
        }

        timeoutId = setTimeout(function () {
            stompClient.send(
                "/app/chat/notTyping",
                {},
                JSON.stringify(
                    {
                        'senderId': currentUser.id,
                        'receiverId': selectedContactId,
                        'typingText': $("#message").val()
                    }
                )
            );
        }, 5000);
    }
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

async function sendMessage() {
    stompClient.send(
        "/app/chat/sendMessage",
        {},
        JSON.stringify(
            {
                'text': $("#message").val(),
                'receiverId': selectedContactId,
                'senderId': currentUser.id
            }
        ));
}

async function getCurrentUser() {
    await fetch('/api/users/me', {method: 'GET'})
        .then(function (response) {
            response.json()
                .then(data => {
                    currentUser = data.data;
                })
        });
}

async function getMyContacts() {
    await fetch('/api/users/myContacts', {method: 'GET'})
        .then(function (responseEntity) {
            responseEntity.json()
                .then(apiResponse => {
                    console.log("=== API RESPONSE DATA === " + apiResponse.data);
                    apiResponse.data.forEach(contact => {
                        appendNewContact(contact)
                    })
                });
        });
}

function getChatMessages(receiverId) {
    fetch('/api/messages/byReceiverId/' + receiverId, {method: 'GET'})
        .then(function (responseEntity) {
            if (responseEntity.ok) {
                console.log("OK");
                responseEntity.json()
                    .then(apiResponse => {
                        apiResponse.data.forEach(message => {
                            appendNewMessage(message);
                        })
                    });
            } else console.log("=== NO MESSAGES ===");
        })
}

function appendNewMessage(message) {
    const messages = document.getElementById("chat-messages");
    // === IF CURRENT USER IS SENDER OF THIS MESSAGE
    if (message.sender.id === currentUser.id) {
        const outgoingMsgDiv = document.createElement('div');
        outgoingMsgDiv.classList.add('outgoing_msg');
        messages.appendChild(outgoingMsgDiv);

        const sentMsgDiv = document.createElement('div');
        sentMsgDiv.classList.add('sent_msg');
        outgoingMsgDiv.appendChild(sentMsgDiv);

        const paragraph = document.createElement('p');
        paragraph.innerText = message.messageText;
        sentMsgDiv.appendChild(paragraph);

        const span = document.createElement('span');
        span.classList.add('time_date');
        span.innerText = message.sentAt;
        sentMsgDiv.appendChild(span);
    } else { // IF MESSAGE SENT BY ANOTHER USER
        const incomingMsgDiv = document.createElement('div');
        incomingMsgDiv.classList.add('incoming_msg');
        messages.appendChild(incomingMsgDiv);

        const incomingMsgImgDiv = document.createElement('div');
        incomingMsgImgDiv.classList.add('incoming_msg_img');
        incomingMsgDiv.appendChild(incomingMsgImgDiv);

        const img = document.createElement('img');
        img.src = 'https://ptetutorials.com/images/user-profile.png';
        img.alt = 'error';
        incomingMsgImgDiv.appendChild(img);

        const receiverMsgDiv = document.createElement('div');
        receiverMsgDiv.classList.add('received_msg');
        incomingMsgDiv.appendChild(receiverMsgDiv);

        const receivedWidthMsgDiv = document.createElement('div');
        receivedWidthMsgDiv.classList.add('received_withd_msg');
        receiverMsgDiv.appendChild(receivedWidthMsgDiv);

        const paragraph = document.createElement('p');
        paragraph.innerText = message.messageText;
        receivedWidthMsgDiv.appendChild(paragraph);

        const span = document.createElement('span');
        span.classList.add('time_date');
        span.innerText = message.sentAt;
        receivedWidthMsgDiv.appendChild(span);
    }
}

function appendNewContact(contact) {
    const chatName = contact.id === currentUser.id ? 'SAVED MESSAGES' : contact.fullName;

    $('#chats').append(
        '<div onclick="chatClickedHandler(' + contact.id + ')" class="chat_list notification" id="' + contact.id + '">' +
        '    <div class="chat_people">' +
        '    <div class="chat_img">' +
        '       <img' +
        '           th:src="@{/api/attachments/download-file/' + contact.imgId + '}"' +
        '           alt="File"' +
        '           width="250px"' +
        '           height="250px"' +
        '       >' +
        '    </div>' +
        '    <div class="chat_ib">\n' +
        '       <h5>' + chatName + ' </h5>' +
        '    </div>' +
        '    </div>' +
        '</div>'
    )

    if (contact.online) {
        $("#" + contact.id + "")
            .css("background-color", "green")
    } else {
        $("#" + contact.id + "")
            .css("background-color", "yellow")
    }
}

function chatClickedHandler(contactId) {
    // AGAR YANGI TANLANGAN CHAT TANLANIB TURGAN CHATGA TENG BO'LMASA
    if (selectedContactId !== null && selectedContactId !== contactId) {
        selectedContactId = contactId;
        $("#chat-messages").empty();

        getChatMessages(selectedContactId);
    } else if (selectedContactId === null) {

        // AGAR HALI HECH QANDAY CHAT TANLANMAGAN BO'LSA UNDA TANLA
        selectedContactId = contactId;
        getChatMessages(selectedContactId);
    }
}
