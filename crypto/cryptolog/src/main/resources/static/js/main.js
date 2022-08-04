function setMenuEntryActive() {
    const targetLink = document.querySelector("a[href='" + window.location.pathname + "']");
    if (targetLink != null) {
        targetLink.classList.add("active");
    }
}

window.onload = function() {
    setMenuEntryActive();
}

/*window.getElementById("submit").onclick = function() {
    let message = document.getElementById("messageSpan");
    if (message = "You have successfully customized the asset allocation in your portfolio. Go to the yield calculator and see how much you could potentially save by investing in crypto.") {
        let yn = confirm(message);
        if(yn) {
        window.location.replace("/calculate")
    } else {
        window.location.replace("/asset")
    }*/
}
