# MOMA_Predictor
## Three methods for handling imbalanced data for classification with decision trees. 

A collaboration with two classmates to train a decision tree that classifies the gender of an artist based on the title of their artwork<sup>*</sup>. We used a publically available Museum of Modern Art (MOMA) dataset containing titles and other metadata about their art collection. 

Example of an entry in the MOMA art dataset:

![](/img/raw_data_example.png)

We used NLTK to bin the data and count gramatical attributes in the title of the artwork. The attributes of titles we counted were nouns, foreign words, prepositions, determiners (like “the”), and adjectives. The output of each line of data is the gender of the artist associated with the title of that work.

Example of processed data:

![](/img/processed_data_format.png)

We first trained a vanilla, unpruned decision tree. 
- We calculated **k-fold accuracy**, which is based on a large training set. (k-fold is: 10% of training set used as validation set) 
- We calculated **test set accuracy**, which is data pulled from our training data. We pulled out 10% of our training data for our test set.
- It’s important to compare these accuries to **random chance**. The gender balance of our dataset is 18:82, women to men. Here, random guessing with knowledge of this proportion would result in 70.48% accuracy.

![](/img/vanilla_results.png)

Results: 
- k-fold and test set accuracy had similar prediction success, and they were both around 11% better than random chance. 
- These accuracies are not bad - 10% above random chance. However, the MOMA dataset is gender imbalanced: 18% woman, and 82% men. So when we train a vanilla decision tree on this data, the tree always predicts men because by doing so, it is correct 82% of the time.

![](/img/baseline_tree.png)

We needed a balanced dataset so that we had the ability to predict female artists. We therefore explored methods for handling imbalanced data. 
The three methods we tried were:
- **Oversampling**: to oversample, we artificially add extra instances of female artists. We do this by copying instances that included female artists in our dataset. Simply copying the same examples is effective in a decision tree where often a choice is made by counting the most common output value. 
  - To test oversampling, we made three different datasets, one with 25% women and 75% men, one with a 50/50 split, and one with 75% women and 25% men.
- **Undersampling**: to undersample, we artificially decrease the instances of male artists. We do this by removing instances of men. 
  - To test undersampling, we made three different datasets with the same splits as oversampling.
- **Ensemble sampling**: to use ensemble sampling we divide our data into 10 sets. Each set has all of the female examples and a random 10% of the men. We then average the accuracy over all the performance on trees trained with each of the ensemble sets.
  - For ensemble testing, we made 10 ensembles with about 1400 lines each.

The other thing we were evaluating was the effect of **post-pruning** on our tree.
- Post-pruning is pruning a tree after training, rather than pruning the tree as you train it. This can improve the accuracy of a tree by reducing overfitting. **Overfitting** is when the decision tree is so customized to the training data that it does not generalize well to the full dataset. Pruning combats this by getting rid of nuances or “twigs” in the tree.
- We used a type of post-pruning called **performance-based post-pruning**. This means we pruned based on the accuracy of a validation set.
For each twig in the tree, post-pruning asks “Does my tree perform better with this twig, or without this twig?”

We compared the performance of the tree on each of these datasets to our baseline model. Here is an example ensemble decision tree. Because the data has been balanced, the tree now also predicts female artists. 

![](/img/ensemble_tree.png)

Here’s an example of an ensemble decision tree after post-pruning. The pruning process snipped off splitting on all attributes except nouns. It replaced these nodes with their plurality, meaning if there were more males we put in a male node and visa versa. Surprisingly, this doesn’t decrease the tree’s accuracy!

![](/img/pruned_tree.png)

Here is a summary of how the various balancing methods compared. 

![](/img/results_summary.png)

For more detailed results, [click here](http://bit.ly/2WHjPOW).



<sup>*</sup>We predict gender based on a binary gender classification because that is the extent of our dataset. We recognize that this does not reflect the full spectrum of gender in reality. We are not assuming that biological sex has anything to do with the way that people write. Instead, based on prior research, we posit that the pressures in society to adequately perform one’s gender may have an impact on one’s text-based self-expression.
