function validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}

window.onload = function () {
    alert(validateEmail("dimitri.ar@qwed.ri"));
    let emailForm = document.getElementById("email");
    let passwordForm = document.getElementById("password");

    let email_valid_err = document.createElement("div");
    email_valid_err.id = "email_valid_err";
    email_valid_err.innerHTML = "Invalid email";
    email_valid_err.className = "error-logout p-t-14 p-l-66";

    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    var qwr = document.getElementById("hello");
    emailForm.oninput = function () {
        qwr.innerHTML = emailForm.value;
        if (re.test(emailForm.value.toLowerCase())) {
            var tmp = document.getElementById("email_valid_err");
            if (tmp) tmp.remove();
        } else
            passwordForm.after(email_valid_err);
    };
}