$('#btn_sign_up').click(function () {
        $(location).attr('href', "http://localhost:8080/CardHibernate/registration.html");
    }
)
$('#btn_login').click(function () {
    $.ajax( {
        method: 'POST',
        url: `login?login=${$('#login').val()}&password=${$('#password').val()}`,
        success: [function (result) {
            $(location).attr('href', "http://localhost:8080/CardHibernate/index.html");
        }],
        error: [function (e){
            alert("error");
        }]
    });
});