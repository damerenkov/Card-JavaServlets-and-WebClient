$('#btn_login').click(function () {
    $(location).attr('href', "http://localhost:8080/CardHibernate/login.html");
})

$('#btn_sign_up').click(function () {
    let login = $('#login').val();
    let password = $('#password').val();
    let name = $('#name').val();
    $.ajax({
        type: 'POST',
        url: 'registration',
        contentType: "application/json",
        dataType: 'json',
        data: JSON.stringify({"login" : login, "password" : password, "name" : name}),
        success: [function (result) {
            $(location).attr('href', "http://localhost:8080/CardHibernate/index.html");
        }],
        error: [function (e) {
            alert(JSON.stringify(e));
        }]
    });
});