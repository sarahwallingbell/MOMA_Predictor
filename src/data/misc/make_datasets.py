import random

def make_test_set(percent, train_file, test_file, original_file):

    orig_file = open(original_file, "r", encoding="utf-8")
    lines = orig_file.readlines()

    trn_file = open(train_file, "w+", encoding = "utf-8")
    tst_file = open(test_file, "w+", encoding = "utf-8")

    counter = 0
    target = round((100/percent)) - 1
    for line in lines:
        if (len(line) > 3): #specific to MOMA dataset
            if (counter == target):
                tst_file.write(line)
                counter = 0;
            else:
                trn_file.write(line)
                counter = counter + 1

    trn_file.close()
    tst_file.close()
    orig_file.close()

def count_binary_output(original_file, output1, output2):
    original_file = open("gender_dataset.csv", "r", encoding="utf-8")
    original_lines = original_file.readlines()
    original_file.close()

    male = 0
    female = 0

    for line in original_lines:
        elements = line.split(",")
        if (output1 in elements[-1]):
            male = male + 1
        if (output2 in elements[-1]):
            female = female + 1

    return male, female

def oversample(orginal_file, percent_male, percent_female, new_file_name):
    male, female = count_binary_output(original_file, "Male", "Female")

    multiplier = percent_male / percent_female
    target_female_lines = round(male / multiplier)

    orig_file = open(original_file, "r", encoding="utf-8")
    lines = orig_file.readlines()
    orig_file.close()

    increased = open("temp.csv", "w+", encoding = "utf-8")
    train_set_name = new_file_name + "_train.csv"
    test_set_name = new_file_name + "_test.csv"
    num_male = 0
    num_female = 0
    while (num_female < target_female_lines):
        for line in lines:
            elements = line.split(",")
            if ("Female" in elements[-1] and num_female < target_female_lines):
                increased.write(line)
                num_female = num_female + 1
            elif ("Male" in elements[-1] and num_male < male):
                increased.write(line)
                num_male = num_male + 1
    make_test_set(10, train_set_name, test_set_name, "temp.csv")
    increased.close()

def undersample(original_file, percent_male, percent_female, new_file_name):
        male, female = count_binary_output(original_file, "Male", "Female")

        multiplier = percent_male / percent_female
        target_male_lines = round(female * multiplier)

        orig_file = open(original_file, "r", encoding="utf-8")
        lines = orig_file.readlines()

        decreased = open("temp.csv", "w+", encoding = "utf-8")
        train_set_name = new_file_name + "_train.csv"
        test_set_name = new_file_name + "_test.csv"

        num_male = 0
        num_female = 0
        while (num_male < target_male_lines):
            for line in lines:
                elements = line.split(",")
                if ("Female" in elements[-1] and num_female < female):
                    decreased.write(line)
                    num_female = num_female + 1
                elif ("Male" in elements[-1] and num_male < target_male_lines):
                    decreased.write(line)
                    num_male = num_male + 1
        make_test_set(10, train_set_name, test_set_name, "temp.csv")
        decreased.close()

def make_ensemble(original_file, num_ensembles, new_file_name):
    orig_file = open(original_file, "r", encoding="utf-8")
    lines = orig_file.readlines()

    male, female = count_binary_output(original_file, "Male", "Female")
    target = male / num_ensembles

    male_lines = []
    female_lines = []

    for line in lines:
        elements = line.split(",")
        if ("Male" in elements[-1]):
            male_lines.append(line)
        if ("Female" in elements[-1]):
            female_lines.append(line)

    for ensemble in range(num_ensembles):
        nfn = new_file_name + str(ensemble+1)
        new_file = open(nfn, "w+", encoding="utf-8")

        ensembled_lines = []
        for fl in female_lines:
            ensembled_lines.append(fl)
        random.shuffle(male_lines)
        line_counter = 0
        for ml in male_lines:
            if line_counter < target:
                ensembled_lines.append(ml)
                male_lines.remove(ml)
            line_counter = line_counter + 1

        random.shuffle(ensembled_lines)
        for line in ensembled_lines:
            new_file.write(line)
        new_file.close()

        new_trn = nfn +"_train.csv"

        new_tst = nfn + "_test.csv"

        make_test_set(10, new_trn, new_tst, nfn)

def main(original_file):
    #make test set for baseline model
    make_test_set(10, "data/baseline/baseline_train.csv", "data/baseline/baseline_test.csv", original_file)

    #make datasets for oversampling
    oversample(original_file, .75, .25, "data/oversample/increased_2575")
    oversample(original_file, .50, .50, "data/oversample/increased_5050")
    oversample(original_file, .25, .75, "data/oversample/increased_7525")

    #make datasets for undersampling
    undersample(original_file, .75, .25, "data/undersample/decreased_2575")
    undersample(original_file, .50, .50, "data/undersample/decreased_5050")
    undersample(original_file, .25, .75, "data/undersample/decreased_7525")

    #make datasets for ensemble
    make_ensemble(original_file, 10, "data/ensemble/ensemble")

if __name__ == "__main__": main()
