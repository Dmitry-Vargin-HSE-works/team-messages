'use strict';

let messageForm = document.querySelector('#messageForm');
let messageInput = document.querySelector('#message');
let messageArea = document.querySelector('#messageAreaParent');
let chatsArea = document.querySelector('#chats');
let chatNameOnTop = document.querySelector('#chatNameOnTop');
let chatNameOnTopSub = document.querySelector('#chatNameOnTopSub');
let searchResults = document.querySelector('#searchResults');
let connectingElement = document.querySelector('.connecting');

let chatsAreasMap = new Map();
let chats = new Map();
let chatsOnLeft = new Map();
let users = new Map();

let stompClient = null;
let chatName = null;
let username = null;
let socket = null;
let ids = [];

let colors = ['#2196F3', '#32c787', '#00BCD4', '#ff5652', '#ffc107',
    '#ff85af', '#FF9800', '#39bbb0'];

function connect() {
    //chatPage.classList.remove('hidden');
    socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
    window.onbeforeunload = function () {
        stompClient.disconnect();
        socket.onclose = function () {
        }; // disable onclose handler first
        socket.close();
    };
}

function onConnected() {
    // Subscribe to the main chat
    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/whoami', false);
    xhr.send();
    username = xhr.responseText;
    xhr.open('GET', '/show/topic', false);
    xhr.send();
    chats = JSON.parse(xhr.responseText);
    for (let chatName of Object.keys(chats)) {
        let chatDiv = document.createElement('div');
        chatDiv.classList.add('convHistory');
        chatDiv.classList.add('userBg');
        chatDiv.classList.add('Chat1');
        chatsAreasMap.set(chatName, chatDiv);
    }
    while (messageArea.hasChildNodes()) {
        messageArea.firstChild.remove();
    }
    messageArea.appendChild(chatsAreasMap.get('main'));
    chatName = 'main';
    chatNameOnTop.textContent = "Main chat"
    chatNameOnTopSub.textContent = "Everyone"
    // document.getElementById("chat-title").textContent = chatName;
    for (let chatName of Object.keys(chats)) {
        stompClient.subscribe('/user/' + username + '/queue/' + chatName, onMessageReceived);
        stompClient.send("/app/chat.join", {}, JSON.stringify({
            chatId: chatName,
            content: "",
            type: 'SYSTEM'
        }))
    }
    stompClient.subscribe('/user/' + username + '/queue/service', onServiceMessageReceived);
    connectingElement.style.display = "none";
    updateChats();
}

function onError(error) {
    connectingElement.style.display = "show";
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    let messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        let chatMessage = {
            chatId: chatName,
            sender: username,
            content: messageInput.value,
            // chatname: chatName,
            type: 'CHAT'
        };
        stompClient.send("/app/sendMessage", {}, JSON
            .stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);
    if (!ids.includes(message.messageId)) {
        let chatid = message.chatId;
        let sender = message.sender;
        let messageElement = document.createElement('div');
        messageElement.id = message.messageId;
        messageElement.classList.add('msg');
        let span = document.createElement('span');
        let senderText = document.createTextNode(message.senderName);
        if (sender === username) {
            messageElement.classList.add('messageSent');
            senderText.textContent = 'You';
            span.style.color = '#02698e'
        } else {
            messageElement.classList.add('messageReceived');
            span.style.color = '#b8eaff'
        }
        let msgText = document.createTextNode(message.content);
        span.style.fontSize = '12px'
        span.appendChild(senderText);
        messageElement.appendChild(span);
        let br = document.createElement('br');
        messageElement.appendChild(br);
        messageElement.appendChild(msgText);
        chatsAreasMap.get(chatid).appendChild(messageElement);
        chatsAreasMap.get(chatid).scrollTop = chatsAreasMap.get(chatid).scrollHeight;
        chatsOnLeft.get(chatid).lastChild.lastChild.textContent = message.content;
        ids.push(message.messageId);
    }
}

function onServiceMessageReceived(payload){
    let serviceMessage = JSON.parse(payload.body);
    if(serviceMessage.content === 'CHATS_UPDATE'){
        updateChats();
    }else if(serviceMessage.content === 'USERS_UPDATE'){
        searchUsers();
    }
}

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    let index = Math.abs(hash % colors.length);
    return colors[index];
}

function updateChats() {
    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/show/topic', false);
    xhr.send();
    chats = JSON.parse(xhr.responseText);
    chatsOnLeft.clear();
    while (chatsArea.hasChildNodes()){
        chatsArea.firstChild.remove();
    }
    for (let chatName of Object.keys(chats)) {
        let chatElem = document.createElement('button');
        let usersInfo = document.createElement('div');
        let chatNameElem = document.createElement('p');
        let chatMsgElem = document.createElement('p');
        let users = 'Main chat';
        if (chatName !== 'main') {
            users = chats[chatName][0] + " & " + chats[chatName][1];
        }
        let chatNameText = document.createTextNode(users);
        let msg = '...';
        if (!chatsAreasMap.has(chatName)){
            let chatDiv = document.createElement('div');
            chatDiv.classList.add('convHistory');
            chatDiv.classList.add('userBg');
            chatDiv.classList.add('Chat1');
            chatsAreasMap.set(chatName, chatDiv);
            stompClient.subscribe('/user/' + username + '/queue/' + chatName, onMessageReceived);
            stompClient.send("/app/chat.join", {}, JSON.stringify({
                chatId: chatName,
                content: "",
                type: 'SYSTEM'
            }))
        }
        if (chatsAreasMap.get(chatName).lastChild != null) {
            msg = chatsAreasMap.get(chatName).lastChild.textContent;
        }
        let chatMsgText = document.createTextNode(msg)
        chatElem.classList.add('usersChat1');
        chatElem.type = 'submit';
        usersInfo.classList.add('usersInfo');
        chatNameElem.classList.add('name');
        chatMsgElem.classList.add('message');
        chatNameElem.appendChild(chatNameText);
        chatMsgElem.appendChild(chatMsgText);
        usersInfo.appendChild(chatNameElem);
        usersInfo.appendChild(chatMsgElem);
        chatElem.appendChild(usersInfo);
        chatElem.id = chatName;
        chatElem.onclick = onChatSelectorClick;
        if (chatName !== "main") {
            chatsArea.appendChild(chatElem);
        } else {
            chatsArea.prepend(chatElem);
        }
        chatsOnLeft.set(chatName, chatElem);
    }
}

function onChatSelectorClick() {
    while (messageArea.hasChildNodes()) {
        messageArea.firstChild.remove();
    }
    chatName = this.id;
    if (chatName === "main") {
        chatNameOnTopSub.textContent = 'Everyone'
        chatNameOnTop.textContent = 'Main chat'
    } else {
        chatNameOnTop.textContent = chats[chatName].join(', ');
        chatNameOnTopSub.textContent = '';
    }
    messageArea.appendChild(chatsAreasMap.get(chatName));
    messageArea.firstChild.scrollTop = messageArea.firstChild.scrollHeight;
}

function searchUsers() {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "/show/users", true);
    xhr.onload = function (e) {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                while (searchResults.hasChildNodes()) {
                    searchResults.firstChild.remove();
                }
                users = JSON.parse(xhr.responseText);
                for (let [name, email] of Object.entries(users)) {
                    if(email !== username){
                        let result = document.createElement('option');
                        result.value = name;
                        result.id = email;
                        searchResults.appendChild(result);
                    }
                }
            } else {
                console.error(xhr.statusText);
            }
        }
    };
    xhr.onerror = function (e) {
        console.error(xhr.statusText);
    };
    xhr.send(null);
}

function createChatWithUser(userEmail){
    let xhr = new XMLHttpRequest();
    let toSend = new Array(userEmail);
    xhr.open("POST", "/kafka/chat/createChat", true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.onload = function (e) {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                updateChats();
            } else if (xhr.status === 409){
                alert('Same chat already exists');
            } else {
                console.error(xhr.statusText);
            }
        }
    };
    xhr.onerror = function (e) {
        console.error(xhr.statusText);
    };
    xhr.send(JSON.stringify(toSend));
}

messageArea.scrollTop;
messageForm.addEventListener('submit', sendMessage, true);
connect();
searchUsers();
let keypress = false;
document.getElementById("search").addEventListener("keydown", (e) => {
    if (e.key) {
        keypress = true;
    } else {
        keypress = false;
    }
});
document.getElementById("search").addEventListener('input', (e) => {
    let value = e.target.value;
    if (keypress === false) {
        // Clicked on option!
        createChatWithUser(users[value]);
        document.getElementById("search").value = '';
    }
    keypress = false;
});