hljs.highlightAll();

function addCursor(id) {
    let text = document.getElementById(id).getElementsByClassName('content')[0].getElementsByTagName('p')[0];
    var cursor = document.createElement('span');
    cursor.classList.add('cursor');

    if (text) {
        text.innerHTML += cursor.outerHTML;
    }
}

function addCursorLast() {
    var divs = document.getElementsByClassName('content');
    var div = divs[divs.length - 1];
    div.getElementsByTagName('p')[0].innerHTML += "<span class=\"cursor\"></span>";
}

function removeCursor() {
    let element = document.querySelector('.cursor');
    if (element) {
        element.remove();
    }
}

function displayLandingView() {
    const wrapper = document.createElement('div');
    wrapper.setAttribute('id', 'landing-view');
    wrapper.appendChild(createElement({
        tagName: 'div', innerHTML: "<h1>Examples</h1>" +
            "<span class=\"example-text\">\"How do I make an HTTP request in Javascript?\"</span>" +
            "<span class=\"example-text\">\"What is the difference between px, dip, dp, and sp?\"</span>" +
            "<span class=\"example-text\">\"How do I undo the most recent local commits in Git?\"</span>" +
            "<span class=\"example-text\">\"What is the difference between stack and heap?\"</span>"
    }));
    document.body.appendChild(wrapper);
}


//替换id为message.getId()的下class为content 的第一个div下的第一个p标签中的内容
function replaceMessageContent(messageId, newContent) {
    let message = document.getElementById(messageId);
    let content = message.getElementsByClassName('content')[0];
    let p = content.getElementsByTagName('p')[0];
    p.innerHTML = newContent;
}

//在class为chat-container的div下追加html
function addMessage(messageHtml) {
    let chatContainer = document.getElementsByClassName('chat-container')[0];
    chatContainer.innerHTML += messageHtml;
}

function scrollToBottom() {
    window.scrollTo({
        top: document.body.scrollHeight,
        behavior: 'smooth'
    });
}

//        String code = "var divs = document.getElementsByClassName('chat-container')[0];var div = document.getElementById('" + message.getId() + "');divs.removeChild(div);";
function removeMessage(messageId) {
    let divs = document.getElementsByClassName('chat-container')[0];
    let div = document.getElementById(messageId);
    divs.removeChild(div);
}
