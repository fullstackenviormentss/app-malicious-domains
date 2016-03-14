$(document).ready(function(){
  $response = $('#responseField');
  $intercept = $('#interceptField');
  $length = $('#lengthField');
  $entropy = $('#entropyField');
  $proVowels = $('#proVowelsField');
  $numWords = $('#numWordsField');
  $logOdds = $('#logOddsField');

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
    
    //replace the following with lambda api endpoint
    var lambdaApiEndpoint = 'https://ysqkdr19hi.execute-api.us-east-1.amazonaws.com/prod/malicious-domain-classifier';
    
    var xhr = $.ajax(lambdaApiEndpoint, xhrSettings)
      .done(function(data){
        console.log(data);
        if(data.errorMessage != null) {
          $response.text('')
          $intercept.text('');
          $length.text('');
          $entropy.text('');
          $proVowels.text('');
          $numWords.text('');
          $logOdds.text('');
        } else {
          if(data.label === 1) {
            $response.text('Malicious');
            $response.css('color', 'red');
          } else {
            $response.text('Legitimate');
            $response.css('color', '#00FF00');
          }
          $intercept.text(data.intercept.toFixed(2));
          $length.text(data.length.toFixed(2));
          $entropy.text(data.entropy.toFixed(2));
          $proVowels.text(data.proVowels.toFixed(2));
          $numWords.text(data.numWords.toFixed(2));
          $logOdds.text((parseFloat(data.intercept) + parseFloat(data.length) + parseFloat(data.entropy) 
          + parseFloat(data.proVowels) + parseFloat(data.numWords)).toFixed(2));
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

