import h2o, os
from h2o.estimators.glm import H2OGeneralizedLinearEstimator

h2o.init()

print('Importing domains data...')
path = os.path.join(os.path.realpath(os.getcwd()), 'legit-dga_domains.csv')
domains = h2o.import_file(path, header=1)

print('Data cleaning...')
domains = domains[~domains['subclass'].isna()]

print('Feature: string length')
domains['length'] = domains['domain'].nchar()

print('Feature: Shannon entropy')
domains['entropy'] = domains['domain'].entropy()

print('Feature: proportion of vowels')
domains['p_vowels'] = 0
for v in 'aeiou':
  domains['p_vowels'] += domains['domain'].countmatches(v)
domains['p_vowels'] /= domains['length']

print('Feature: proportion of substrings >=2 chars that are English words')
english_words = os.path.join(os.path.realpath(os.getcwd()),'src','main','resources','words.txt')
domains['p_substrings_words'] = domains['domain'].pro_substrings_words(english_words)

print('\nResponse: Is domain malicious?')
domains['malicious'] = domains['class'] != 'legit'

rand = domains.runif(seed=123456)
train = domains[rand <= 0.8]
valid = domains[rand > 0.8]

print('\nModel: Logistic regression with regularization')
model = H2OGeneralizedLinearEstimator(model_id='MaliciousDomainModel',
                                      family='binomial', alpha=0, Lambda=1e-5)

model.train(x=['length', 'entropy', 'p_vowels', 'p_substrings_words'],
            y='malicious', training_frame=train, validation_frame=valid)

print(model.confusion_matrix(valid=True))

print('Download generated POJO for model')

model.download_pojo(path='lib')

h2o.shutdown(prompt=False)

