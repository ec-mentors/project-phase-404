function setMenuEntryActive() {
    const targetLink = document.querySelector("a[href='" + window.location.pathname + "']");
    if (targetLink != null) {
        targetLink.classList.add("active");
    }
}

window.onload = function() {
    setMenuEntryActive();
}
