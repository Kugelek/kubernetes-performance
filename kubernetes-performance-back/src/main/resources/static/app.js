var redirect_uri = "http://127.0.0.1:8080/start.html"

var client_id = ""
var client = ""

const AUTHORIZE = "https://accounts.spotify.com/authorize"

function login() {
    client_id = document.getElementById("clientId").value
    client_secret = document.getElementById("clientSecret").value

    let url = AUTHORIZE
    url += "?client_id"


}
