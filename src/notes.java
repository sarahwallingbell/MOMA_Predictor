//# First pass : evaluate validation data and note the classification at each node
//# Assuming that "valData" includes "attributes" and "labels"
//
//# First create an empty list of error counts at nodes
//def createNodeList(dTree, nodeError=[]):
//    nodeError[dTree] = 0
//    for child in dTree.@children
//        createNodeList(dTree, nodeError)
//    return nodeError
//
//# Pass a single instance down the tree and note node errors
//def classifyValidationDataInstance(dTree, validationDataInstance, nodeError):
//    if (dTree.@majorityClass != validationDataInstance.label):
//        nodeError[dTree] += 1
//    if (not @isLeaf):
//        childNode = dTree.@children[testAttributes[@bestAttribute]]
//        classifyValidationDataInstance(childNode, testAttributes, nodeError)
//    return 
//
//# Count total node errors for validation data
//def classifyValidationData(dTree, validationData):
//    nodeErrorCounts = createNodeList(dTree)
//    for instance in validationData:
//        classifyValidationDataInstance(child, instance, nodeErrorCounts)
//    return nodeErrorCounts
//
//
//# Second pass:  Create a heap with twigs using nodeErrorCounts
//def collectTwigsByErrorCount(decisionTree, nodeErrorCounts, heap=[])
//    if isTwig(decisionTree):
//        # Count how much the error would increase if the twig were trimmed
//        twigErrorIncrease = nodeErrorCounts[decisionTree]
//        for child in decisionTree.@children
//            twigErrorIncrease -= nodeErrorCounts[child]
//        heappush(heap,(twigErrorIncrease, decisionTree))
//    else
//        for each child in decisiontree.children:
//            collectTwigsByErrorCount(child, nodeErrorCounts, heap)
//    return heap
//
//
//
//# Third pass: Prune a tree to have nLeaves leaves
//# Assuming heappop pops smallest value
//def pruneByClassificationError(dTree, validationData, nLeaves):
//    # First obtain error counts for validation data
//    nodeErrorCounts = classifyValidationData(dTree, validationData)
//    # Get Twig Heap
//    twigHeap = collectTwigsByErrorCount(dTree, nodeErrorCounts)
//
//    totalLeaves = countLeaves(dTtree)
//    while totalLeaves > nLeaves:
//        twig = heappop(twigHeap)
//        totalLeaves -= (length(twig.@children) - 1) #Trimming the twig removes
//                                                    #numChildren leaves, but adds
//                                                    #the twig itself as a leaf
//        twig.@chidren = null  # Kill the chilren
//        twig.@isLeaf = True
//        twig.@nodeInformationGain = 0
//
//        # Check if the parent is a twig and, if so, put it in the heap
//        parent = twig.@parent
//        if isTwig(parent):
//            twigErrorIncrease = nodeErrorCounts[parent]
//            for child in parent.@children
//                twigErrorIncrease -= nodeErrorCounts[child]
//            heappush(twigHeap,(twigErrorIncrease, parent))
//    return