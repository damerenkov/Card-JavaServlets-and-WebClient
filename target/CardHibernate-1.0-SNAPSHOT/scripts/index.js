let user;
let category_Id;
let card_Id;
let allCategories;
let categoryCards;

function getUser() {
    let userId = $.cookie('user');
    if (userId === undefined) {
        alert("error");
    } else {
        let number = parseInt(userId);
        $.ajax({
            type: 'GET',
            url: `login?id=${number}`,
            success: [function (result) {
                let res = result.data;
                user = res;
            }],
            error: [function (e) {
                alert("Incorrect User");
            }]
        })
    }
}

function showCategories() {
    $('#tbody_category').html("");
    let userId = $.cookie('user');
    if (userId === undefined) {
        alert("error");
    }
    $.ajax({
        type: 'GET',
        url: `category?user=${userId}`,
        contentType: 'application/json',
        success: [function (result) {
            let categories = result.data;
            allCategories = categories;
            for (let i = 0; i < categories.length; i++) {
                let markup = `<tr id="${categories[i].id}">` +
                    `<td>${categories[i].id}</td>` +
                    `<td>${categories[i].name}</td>` +
                    '</tr>';
                $('#tbody_category').append(markup);
            }
            selectCategories();
        }],
        error: [function (e) {
            alert(JSON.stringify(e));
        }]
    });
}

function showCards(categoryID) {
    $('#tbody_card').html("");
    $.ajax({
        type: 'GET',
        url: `card?category=${categoryID}`,
        success: [function (result) {
            let cards = result.data;
            categoryCards = cards;
            for (let i = 0; i < cards.length; i++) {
                let markup = `<tr id="${cards[i].id}">` +
                    `<td>${cards[i].id}</td>` +
                    `<td>${cards[i].question}</td>` +
                    `<td>${cards[i].answer}</td>` +
                    `<td>${cards[i].category.id}</td>` +
                    `<td>${cards[i].creationDate}</td>` +
                    '</tr>';
                $('#tbody_card').append(markup);
            }
        }],
        error: [function (e) {
            alert("Incorrect Cards")
        }]
    });
}

function selectCategories() {
    for (let i = 0; i < allCategories.length; i++) {
        let markup = `<option value="${allCategories[i].id}">${allCategories[i].name}</option>`;
        $('#category_list').append(markup);
    }
}

$(document).ready(function () {

    getUser();
    showCategories();

    $('#btn_sign_out').click(function () {
        $.ajax({
            type: 'PUT',
            url: `login?id=${user.id}`,
            success: [function (result) {
                $(location).attr('href', "http://localhost:8080/CardHibernate/login.html");
            }],
            error: [function (e) {
                alert("Incorrect Sign out");
            }]
        });
    });

    $('#tbody_category').on("click", "tr", function () {
        category_Id = $(this).attr('id');
        showCards(category_Id);
    });

    $('#tbody_card')
        .on("click", "tr", function () {
            card_Id = $(this).attr('id');
        })
        .on("dblclick", "tr", function () {
            $('#modalChangeCard').modal('show');
            let id = $(this).attr('id');
            let selected_id = parseInt(id);
            let selectedCard = categoryCards.find(c => c.id === selected_id);

            $('#change_card_id').val(selectedCard.id);
            $('#change_card_question').val(selectedCard.question);
            $('#change_card_answer').val(selectedCard.answer);
            $('#change_card_category').attr('value', selectedCard.category.id);
            $('#change_card_category').val(selectedCard.category.name);
            $('#change_card_date').val(selectedCard.creationDate);
        });

    $('#change_card_button').click(function () {
        let id = $('#change_card_id').val();
        let question = $('#change_card_question').val();
        let answer = $('#change_card_answer').val();
        let category = $('#change_card_category').attr('value');
        let date = $('#change_card_date').val();
        category = parseInt(category);
        console.log(category);
        let find = allCategories.find(c => c.id === category);
        $.ajax({
            type: 'PUT',
            url: `card`,
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({
                "id": id,
                "question": question,
                "answer": answer,
                "category": find,
                "creationDate": date
            }),
            success: [function () {
                showCards(category);
            }],
            error: [function (e) {
                alert(JSON.stringify(e));
            }]
        });
    });

    $('#delete_category').click(function () {
        if (category_Id !== undefined) {
            $.ajax({
                type: 'DELETE',
                url: `category?id=${category_Id}`,
                success: [function () {
                    showCategories();
                }],
                error: [function () {
                    alert("error");
                }]
            });
        }
    });

    $('#add_category_button').click(function () {
        let name = $('#add_category_name').val();
        $.ajax({
            type: 'POST',
            url: 'category',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({
                "name": name,
                "user": user
            }),
            success: [function (result) {
                showCategories();
            }],
            error: [function (e) {
                alert("Incorrect category post");
            }]

        });
    });

    $('#add_card_button').click(function () {
        let question = $('#add_card_question').val();
        let answer = $('#add_card_answer').val();
        let categoryId = $('#category_list').find('option:selected').attr('value');
        parseInt(categoryId);
        $.ajax({
            type: 'POST',
            url: 'card',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({
                "question": question,
                "answer": answer,
                "category": {
                    "id": categoryId
                }
            }),
            success: [function (result) {
                showCards(categoryId);
            }],
            error: [function (e) {
                alert(JSON.stringify(e));
            }]
        });
    });

    $('#delete_card').click(function () {
        if (card_Id === undefined) {
            alert("error");
        } else {
            parseInt(card_Id);
            $.ajax({
                type: 'DELETE',
                url: `card?id=${card_Id}`,
                success: [function (result) {
                    let card = result.data;
                    showCards(card.category.id);
                }],
                error: [function () {
                    alert('error');
                }]
            })
        }
    });
})