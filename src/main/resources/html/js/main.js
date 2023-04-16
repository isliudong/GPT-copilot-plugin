hljs.highlightAll();
function addCursor(id) {
    let text = document.getElementById(id).getElementsByClassName('content')[0].getElementsByTagName('p')[0];
    var cursor = document.createElement('span');
    cursor.classList.add('cursor');

    if (text){
        text.innerHTML+=cursor.outerHTML;
    }
}
function removeCursor() {
    let element = document.querySelector('.cursor');
    if (element) {
        element.remove();
    }
}

function displayLandingView () {
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
