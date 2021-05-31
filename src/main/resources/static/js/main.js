'use strict';

let usernamePage = document.querySelector('#username-page');
let chatPage = document.querySelector('#chat-page');
let messageForm = document.querySelector('#messageForm');
let messageInput = document.querySelector('#message');
let messageArea = document.querySelector('#messageAreaParent');
let chatsArea = document.querySelector('#chats');
let connectingElement = document.querySelector('.connecting');
let chatsAreasMap = new Map();

let stompClient = null;
let chatName = null;
let username = null;
let subscriptions = [];

let colors = ['#2196F3', '#32c787', '#00BCD4', '#ff5652', '#ffc107',
    '#ff85af', '#FF9800', '#39bbb0'];

function connect() {
    //chatPage.classList.remove('hidden');
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    // Subscribe to the main chat
    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/whoami', false);
    xhr.send();
    username = xhr.responseText;
    xhr.open('GET', '/show/topic', false);
    xhr.send();
    let response = JSON.parse(xhr.responseText);
    for (let chatName of Object.keys(response)) {
        let chatDiv = document.createElement('div');
        chatDiv.classList.add('convHistory');
        chatDiv.classList.add('userBg');
        chatDiv.classList.add('Chat1');
        chatsAreasMap.set(chatName, chatDiv);
    }
    while (messageArea.hasChildNodes()){
        messageArea.firstChild.remove();
    }
    messageArea.appendChild(chatsAreasMap.get('main'));
    chatName = 'main';
    // document.getElementById("chat-title").textContent = chatName;
    for (let chatName of Object.keys(response)) {
        stompClient.subscribe('/user/' + username + '/queue/' + chatName, onMessageReceived);
        stompClient.send("/app/chat.join", {}, JSON.stringify({
            chatId: chatName,
            content: "",
            type: 'SYSTEM'
        }))
    }
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
    let chatid = message.chatId;
    let sender = message.sender;
    let messageElement = document.createElement('div');
    messageElement.classList.add('msg');
    if (sender === username) {
        messageElement.classList.add('messageSent');
    } else {
        messageElement.classList.add('messageReceived');
    }
    let msgText = document.createTextNode(message.content);
    messageElement.appendChild(msgText);
    chatsAreasMap.get(chatid).appendChild(messageElement);
    chatsAreasMap.get(chatid).scrollTop = chatsAreasMap.get(chatid).scrollHeight;
    /*    if (message.type === 'JOIN') {
            messageElement.classList.add('event-message');
            message.content = message.sender + ' joined!';
        } else if (message.type === 'LEAVE') {
            messageElement.classList.add('event-message');
            message.content = message.sender + ' left!';
        } else {
            messageElement.classList.add('chat-message');
            let avatarElement = document.createElement('i');
            let avatarText = document.createTextNode(message.sender[0]);
            avatarElement.appendChild(avatarText);
            avatarElement.style['background-color'] = getAvatarColor(message.sender);
            messageElement.appendChild(avatarElement);
            let usernameElement = document.createElement('span');
            let usernameText = document.createTextNode(message.sender);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
        }
        let textElement = document.createElement('p');
        let messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);
        messageElement.appendChild(textElement);
        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;*/
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
    let response = JSON.parse(xhr.responseText);
    for (let chatName of Object.keys(response)) {
        let chatElem = document.createElement('button');
        let usersInfo = document.createElement('div');
        let chatNameElem = document.createElement('p');
        let messageText = document.createTextNode(chatName);
        chatElem.classList.add('usersChat1');
        chatElem.type = 'submit';
        usersInfo.classList.add('usersInfo');
        chatNameElem.classList.add('name');
        chatNameElem.appendChild(messageText);
        usersInfo.appendChild(chatNameElem);
        chatElem.appendChild(usersInfo);
        chatElem.onclick = onChatSelectorClick;
        chatsArea.appendChild(chatElem);
    }
}

function onChatSelectorClick() {
    while (messageArea.hasChildNodes()) {
        messageArea.firstChild.remove();
    }
    chatName = this.firstChild.firstChild.firstChild.nodeValue
    messageArea.appendChild(chatsAreasMap.get(chatName));
    messageArea.firstChild.scrollTop = messageArea.firstChild.scrollHeight;
}

messageArea.scrollTop;
messageForm.addEventListener('submit', sendMessage, true);
connect();
