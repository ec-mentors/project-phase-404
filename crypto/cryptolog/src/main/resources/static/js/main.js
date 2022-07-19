function setMenuEntryActive() {
    // find the link matching browser-url
    //const targetLink = $("a[href='" + window.location.pathname + "']");
    const targetLink = document.querySelector("a[href='" + window.location.pathname + "']");
    // set targetLink active
    //$(".nav li a").removeClass("active");
    //targetLink.addClass("active");
    targetLink.classList.add("active");
}

window.onload = function() {
    setMenuEntryActive();
}
