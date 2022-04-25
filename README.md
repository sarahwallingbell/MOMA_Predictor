# MOMA_Predictor
Three methods for handling imbalanced data for classification with decision trees. 

A collaboration with two classmates to train a decision tree that classifies the gender of an artist based on the title of their artwork<sup>*</sup>. We used a publically available Museum of Modern Art (MOMA) dataset containing titles and other metadata about their art collection. 

Example of an entry in the MOMA art dataset:
![](/img/raw_data_example.png)

We used NLTK to bin our title and count the attributes in the title of the artwork. 
The attributes of titles we counted were nouns, foreign words, prepositions, determiners (like “the”), and adjectives. The output of each line of data is the gender of the artist associated with the title of that work.

Example of processed data:
![](/img/processed_data_format.png)

We first trained a vanilla, unpruned decision tree. These are the results we got.
- We calculated k-fold accuracy, which is based on a large training set. (remind people what k-fold is: 10% of training set used as validation set) 
- We calculated test set accuracy, which is data pulled from our training data. We pulled out 10% of our training data for our test set.
- It’s important to compare these accuries to random chance. The gender balance of our dataset is 18:82, women to men. Here, random guessing with knowledge of this proportion would result in 70.48% accuracy.
- Results: k-fold and test set accuracy had similar prediction success, and they were both around 11% better than random chance. 
- These accuracies are not bad- 10% above random chance. However, our tree is about 82% accurate. Our test set is 82% male. The problem is that the tree never predicts female.
![](/img/vanilla_results.png)


This is the tree we got from our baseline training data. It’s important to note that all of the leaf nodes predict the artist is male. This is due to the imbalance in our data (18F:82M). Because of  this result, in order to generate a better decision tree, we decided to explore methods for handling imbalanced data. 
The data on artists from the MOMA is very imbalanced. The data is composed of 18% woman, and 82% men. When we train a vanilla decision tree on just this data, the tree always predicts men because by doing so, it is correct 82% of the time.
We need a balanced data set so that we have the ability to predict female artists. 
![](/img/baseline_tree.png)


The three methods we tried are:
Oversampling: to oversample, we artificially add extra instances of female artists. We do this by copying instances that included female artists in our dataset. Simply copying the same examples is effective in a decision tree where often a choice is made by counting the most common output value. (Randomly chose women to erase (shuffled data before adding).)
Undersampling: to undersample, we artificially decrease the instances of male artists. We do this by removing instances of men. [Randomly chose men to erase, (shuffled data before deleting).]
Ensemble sampling: to use ensemble sampling we divide our data into 10 sets. Each set has all of the female examples and a random 10% of the men. We then average the accuracy over all the performance on trees trained with each of the ensemble sets.



The other thing we are evaluating is the effect of post-pruning on our tree.
Post-pruning is rather than pruning your tree as you train it, you prune a tree after training. 
Post-pruning can improve the accuracy of a tree by reducing overfitting.

Overfitting is when the decision tree is very customized or fitted to the training data, so when it is given a piece of new data is actually doesn’t do well. Pruning combats this because it gets rid of nuances or “twigs” in our tree so that it can generalize to other data.

We are using a type of post-pruning called performance-based post-pruning. 
This means we are pruning based on the accuracy of a validation set.
For each twig in the tree, post-pruning asks “Does my tree perform better with this twig, or without this twig?”



These are the datasets we created to compensate for imbalanced data.
To test oversampling, we made three different datasets, one with 25% women and 75% men, one with a 50/50 split, and one with 75% women and 25% men.
To test undersampling, we made three different datasets with the same splits.
For ensemble testing, we made 10 ensembles with about 1400 lines each.
We compared the performance of the tree on each of these datasets to our baseline model, which we talked about at the beginning.
![](/img/ensemble_tree.png)



Here’s an example of a tree after using pruning! As you can see, the pruning process snipped off splitting on every over type of word other than number of nouns, and simply replaced those nodes with their plurality value meaning if there were more males we put in a male node and visa versa. Surprisingly, this doesn’t decrease the tree’s accuracy...
![](/img/pruned_tree.png)



Here’s an example of a tree after using ensemble! Notice that it now includes the ability to predict female artists. 
Let’s talk about how much these different balancing strategies (Oversampling, Undersampling, Ensemble) actually helped by looking at the data. [Next slide]












<sup>*</sup>We predict gender based on a binary gender classification because that is the extent of our dataset. We recognize that this does not reflect the full spectrum of gender in reality. We are not assuming that biological sex has anything to do with the way that people write. Instead, based on prior research, we posit that the pressures in society to adequately perform one’s gender may have an impact on one’s text-based self-expression.
