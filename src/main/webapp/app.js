$(document).ready(function(){
  $response = $('#responseField');

  function predict() {
    var data = {};
    
    $('input').each(function(){
      var $field = $(this);
      data[$field.attr('data-column')] = $field.val();
    });
    
    var xhrSettings = {
      'data':JSON.stringify(data),
      'type':'POST',
      'dataType':'json',
      'processData':false,
    }
    
    
//
//    $('select').each(function(){
//      var $field = $(this);
//      data[$field.attr('data-column')] = $field.val();
//    });

    var lambdaApiEndpoint = 'https://ysqkdr19hi.execute-api.us-east-1.amazonaws.com/prod/malicious-domain-classifier';
    
    var xhr = $.ajax(lambdaApiEndpoint, xhrSettings)
      .done(function(data){
        console.log(data);
        if(data.label === 1) {
          $response.text('Malicious!');
        } else {
          $response.text('Legitimate');
        }
      })
      .fail(function(error){
        console.error(error.responseText ? error.responseText : error);
        $response.text('(Invalid input)');
      })
  }

  var updatePrediction = _.debounce(predict, 250);

  $('input').each(function(){
    $(this).keydown(updatePrediction);
  });

  $('select').each(function(){
    $(this).change(updatePrediction);
  });

  $("form").bind("keypress", function (e) {
    if (e.keyCode == 13) {
      return false;
    }
  });

  updatePrediction();

});

