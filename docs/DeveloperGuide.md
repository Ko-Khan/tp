

---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# PressPlanner Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* GitHub CoPilot was used to help speed up tasks like writing documentation, writing JavaDocs, creating TestUtil Classes and autocompleting repetitive method calls. It was also used at times to troubleshoot bugs and help to generate difficult parts of the code (e.g. regex expressions for parsing commands with differing requirements)


* Some less commonly used packages of the Java's standard library were also used which includes the java.awt library and the java.net package which are used to implement the `link article` feature.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="1500"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object. If the command deals with Articles then the `AddressBookParser` will pass it to the `ArticleBookParser` which will then handle to command similarly to the `AddressBookParser`.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="1500" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="1000" />

The `Storage` component,
* can save address book data, article book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from `AddressBookStorage`, `ArticleBookStorage` and `UserPrefStorage`, which means it can be treated as any one of these (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.


### \[Implemented\] Sort persons feature

#### Implementation

The sort persons command is implemented in the `SortCommand` class. The `SortCommand` class is a subclass of the `PersonCommand` class, which is in turn a subclass of the `Command` class. The `SortCommand` class is responsible for sorting the persons in the address book according to a field present in the `Person` class by overriding the following method in the `Command` class:

* `execute(Model model)` — Sorts the persons in the address book according to a field present in the `Person` class which is mapped to the `prefix` field in the `SortCommand` object.

Given below is an example usage scenario and how the sort persons feature behaves at each step.

Step 1. The user launches the application for the first time. The `AddressBook` will be initialized with the initial address book state.

Step 2. The user executes `sort n/` command. The `sort` command calls `Model#sortAddressBook("n/")` which sorts the persons in the address book by name in increasing lexicographical order.

The following sequence diagram shows how a sort persons operation goes through the `Logic` component:

<puml src="diagrams/SortPersonsSequenceDiagram-Logic.puml" alt="SortPersonsSequenceDiagram-Logic" />

Similarly, how a sort persons operation goes through the `Model` component is shown below:

<puml src="diagrams/SortPersonsSequenceDiagram-Model.puml" alt="SortPersonsSequenceDiagram-Model" />

#### Design considerations:

**Aspect: How sort persons executes:**

* **Alternative 1 (current choice):** Directly sort the `internalList` field present in the `UniquePersonList` object.
    * Pros: Easy to implement.
    * Cons: Permanently orders all persons in the `AddressBook` by the `Person` field specified by the related `prefix`.


* **Alternative 2:** Clone the current `internalList` field
  present in the `UniquePersonList` object, sort the clone, then replace the `internalUnmodifiableList` field in the `UniquePersonList` object with the sorted clone.
    * Pros: Will not permanently order all persons in the `AddressBook` by the `Person` field specified by the related `prefix`, but only the current view displayed to the user which is refreshed for every opening of the application or commands that changes the view (e.g. `List`, `Find` commands).
    * Cons: Takes up much more memory space directly proportional to the size of the `AddressBook` since a clone of all `Persons` has to be made.

### \[Implemented\] Filter feature

#### Implementation

<puml src="diagrams/ModelFilterClassDiagram.puml" alt="ModelFilterClassDiagram" />

The filter mechanism is facilitated by `filter` interface. The ArticleFilter and PersonFilter classes will inherit from it.
The filters will store `Predicate<>` objects that will determine which Persons or articles will be shown to the user.
`ModelManager` will contain a filter, which it will use to generate `FilteredLists`

Given below is an example usage scenario:

Step 1. The user launches the application. The `ModelManager` will be initialized, along with the Filter objects it contains. `finalPredicate` will be set to display all articles for now.  

Step 2. The user executes `filter -a s/ st/ en/ t/DRAFT` to look for articles he is currently working on.  The filter article command gets the `ArticleFilter` object using `getFilter()`. Then it updates the filter object by calling the `updateFilter()` method, changing the `finalPredicate`.


<puml src="diagrams/FilterSequenceDiagram.puml" alt="FilterSequenceDiagram" />

Step 3. Now that the filter has been updated. The user now looks through PressPlanner to search for the article. He decides to search by title to make it faster. He executes `find -a AI`. Beyond matches with the name, PressPlanner is still filtering to show only DRAFT articles, allowing the user to search a smaller set.

Step 4. The user has found his article and wishes to remove the filter. He does this by executing `set -a S/ ST/ EN/`. With no instructions, the predicate allows all articles to pass through the filter.  

Note: If start date is later than the end date, PressPlanner will refuse to execute the command, double-check the dates to avoid this scenario.  

Note: Filters are **NOT** stored by the program. If you close the app, your filters will be reset. 

### \[Implemented\] Lookup Commands

#### Implementation

The proposed lookup feature is enabled by altering `Person` and `Article` classes to store a list of `Article` and `Person` objects respectively. The `Person` class will have a `List<Article>` attribute that stores the articles that the person is involved in. The `Article` class will have a `List<Person>` attribute that stores the persons involved in the article.

When a `Person` object is added/edited, the `Model` component will check if the name of the person `Person` matches the name of the contributors/interviewees in any `Article` in the `ArticleBook`. If it does, the `Person` object will be updated with new `Article` objects in the list of articles it contains. Editing the name will also change the name of the corresponding contributor/interviewee in the `Article` objects. Adding a `Article` object work similarly, but in reverse. However, editing the name of contributors/interviewees in the `Article` objects will not update the corresponding names `Person` objects.

The following diagram shows how the LookupCommand is executed:

<puml src="diagrams/LookupSequenceDiagram-Logic.puml" alt="Implemented Sequence Diagram for LookupCommand" />

<puml src="diagrams/LookupSequenceDiagram-Model.puml" alt="Implemented Sequence Diagram for LookupCommand" />

The ArticlesInPersonPredicate tests the articles by checking whether the list of articles within the `Person` object contains the article being tested. The PersonsInArticlePredicate tests the persons by checking whether the list of persons within the `Article` object contains the article being tested.

#### Design considerations:

Aspect: How to store associations between `Person` and `Article` objects:

* **Alternative 1 (current choice):** Do not store any associations between `Person` and `Article` objects. Instead, since everytime the app is opened it reads all the persons and articles and adds them to the `Model`, the `Model` will always recreate the associations between `Person` and `Article` objects.
    * Pros: Easier to implement. Uses less storage.
    * Cons: Could slow down lookup time.
* **Alternative 2:** Store associations between `Person` and `Article` objects in a separate `AssociationStorage` object in the `Storage` component.
    * Pros: Faster lookup time.
    * Cons: More complex to implement and maintain. Uses more storage.

Aspect: What criteria to use to create associations between `Person` and `Article` objects:

* **Alternative 1 (current choice):** Use the name of the `Person` object to match with the name of the contributors/interviewees in the `Article` objects.
    * Pros: Easier to implement.
    * Cons: Does not allow for multiple persons with the same name to be associated with different articles, making the user work around this limitation by altering the names slightly.
* **Alternative 2:** Use a unique identifier like an id for each `Person` object to match with the contributors/interviewees in the `Article` objects.
    * Pros: Allows for multiple persons with the same name to be associated with different articles.
    * Cons: More complex to implement.

Aspect: How editing the names affects the associated objects:

* **Alternative 1a (current choice):** Editing the name of a `Person` object will also change the name of the corresponding contributor/interviewee in the `Article` objects.
    * Pros: Changes in name is automatically reflected in the `Article` objects.
    * Cons: Could lead to unintended changes in the `Article` objects.
* **Alternative 1b:** Editing the name of a `Person` object will not change the name of the corresponding contributor/interviewee in the `Article` objects.
    * Pros: Prevents unintended changes in the `Article` objects.
    * Cons: Could lead to inconsistencies between the `Person` and `Article` objects.

### \[Implemented\] Sort articles feature

#### Implementation

This feature closely follows the logic and model design of the sort persons feature mentioned above, by referencing the article classes that are implemented similar to their person counterparts as listed below.

The differences include:

1. `SortArticleCommand` class inherits from the `ArticleCommand` class which in turn inherits from the `Command` class instead of the `SortCommand` class.
1. `SortArticleCommandParser` instead of `SortCommandParser` class.
1. `AddressBookParser` passes command flow to the `ArticleBookParser` class.
1. Sorting will be done on the `ArticleBook` object instead of the `AddressBook` object.
1. The execution of the `sort -a d/` command will invoke the `Model#sortArticleBook("d/")` method instead of the `Model#sortAddressBook("d/")` method.

The following sequence diagram shows how a sort articles operation goes through the Logic component:

<puml src="diagrams/SortArticlesSequenceDiagram-Logic.puml" alt="SortArticlesSequenceDiagram-Logic" />

Similarly, how a sort articles operation goes through the `Model` component is shown below:

<puml src="diagrams/SortArticlesSequenceDiagram-Model.puml" alt="SortArticlesSequenceDiagram-Model" />

#### Design considerations:

**Aspect: How sort articles executes:**

* **Alternative 1 (current choice):** Directly sort the `internalList` field present in the `UniqueArticleList` object.
    * Pros: Easy to implement.
    * Cons: Permanently orders all articles in the `ArticleBook` by the `Article` field specified by the related `prefix`.


* **Alternative 2:** Clone the current `internalList` field
  present in the `UniqueArticleList` object, sort the clone, then replace the `internalUnmodifiableList` field in the `UniqueArticleList` object with the sorted clone.
    * Pros: Will not permanently order all articles in the `ArticleBook` by the `Article` field specified by the related `prefix`, but only the current view displayed to the user which is refreshed for every opening of the application or commands that changes the view (e.g. `List`, `Find` commands).
    * Cons: Takes up much more memory space directly proportional to the size of the `ArticleBook` since a clone of all `Articles` has to be made.

### \[Implemented\] Link Webpage to Articles

#### Implementation

The link feature is implemented in the `ArticleCard` class, so that when the user clicks on the link button, the link of the article on the article card will be opened.
The link feature is enabled by filling up `link` attribute of `Article` class when adding an article. This feature creates a link button on the UI of each `Article` that opens up a web browser and directs the user to the webpage of where the actual article is uploaded.
Since the `Articlebook` does not store the whole content of the articles, users will be able to read the articles using this feature.

Given below is an example usage scenario:

Step 1. The user launches the application for the first time. The `ArticleBook` will be initialized with the initial article book state.

Step 2. The user executes `add -a h/Article1 d/20-03-2024 s/draft l/https://www.article1.com` command to add a new article. The `add` command calls `Logic#addArticleCommand("Article1", 20-03-2024, draft "https://www.article1.com")` which adds the article to the `ArticleBook`.

Step 3. Notice that the `link` attribute of the `Article` object is filled with the link provided by the user.

Step 4. The user clicks on the link button on the UI of the `Article` object. The link button will open up a web browser and direct the user to the webpage of where the actual article is uploaded.

<puml src="diagrams/LinkSequenceDiagram.puml" alt="LinkSequenceDiagram" />


#### Design Considerations

**Aspect: How the link feature is implemented:**

* **Alternative 1 (current choice):** The link feature is implemented in the `ArticleCard` class.
    * Pros: Easy to implement.
    * Cons: The link feature is not reusable for other classes.

* **Alternative 2:** The link feature is implemented in a separate class.
    * Pros: The link feature is reusable for other classes.
    * Cons: More complex to implement.

The class diagram below shows how the `Article` will look and interact after implementation of the link feature.

<puml src="diagrams/LinkClassDiagram.puml" alt="LinkClassDiagram"/>

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Templating of Articles

#### Proposed Implementation

The proposed templating feature is enabled by creating a `Template` superclass that `Article` inherits from.

The `Template` class will have the following attributes:
- `Name` - The name of the template.
- `Authors` - A list of `Person` objects that are the authors of the article.
- `Sources` - A list of `Person` objects that are the sources of the article.
- `Tags` - A list of tags that are associated with the article.

The `Template` class will also have getter methods for accessing each of the attributes.

The `Article` class will be augmented to have the `applyTemplate` method, which will take a `Template` object as an argument and apply the template to the article. This will involve setting the `Authors`, `Sources`, `Tags`, and `Status` attributes of the article to the corresponding attributes of the template if the values are not `null`.

The `Model` component will be augmented with a `UniqueTemplateList` to store the templates. The `Model` will also have methods to add, delete, and list templates.

#### Proposed Implementation

Step 1. The user creates a new `Template` object by entering the correct CLI input and providing the index of an article and the attributes to be used in the template.

Step 2. `MakeTemplateCommandParser` parses the attribute prefixes and corresponding values from the user input.

Step 3. `MakeTemplateCommand` is created with the parsed attributes.

Step 4. The newly made `Template` object is added to the `UniqueTemplateList` in the `Model` component and throws an error if there is a duplicate.

Step 5. The user can apply the template to an article by entering the correct CLI input and providing the index of the article and the index of the template.

Step 6. `ApplyTemplateCommandParser` parses the indexes from the user input.

Step 7. `ApplyTemplateCommand` is created with the parsed indexes.

Step 8. The `ApplyTemplateCommand` is executed, and the template is applied to the article.

The following sequence diagram shows how the `MakeTemplateCommand` is executed:

<puml src="diagrams/ProposedTemplateSequenceDiagram.puml" alt="Proposed Sequence Diagram for MakeTemplateCommand" />


### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* freelance journalists
* has a need to manage a significant number of contacts for different facets of business
* prefer using text-based commands than multistep GUI
* can type fast
* value speed and efficiency

**Value proposition**: An app for freelance journalists that can streamline their workflow by organizing sources, tracking outlets interested in their stories, and managing collaborations with peers/editors. With features like tagging and grouping contacts, it facilitates efficient research, ensuring reporters can quickly reach out and report on breaking stories.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                     | I want to …​                           | So that I can…​                                                                                |
|----------|---------------------------------------------|----------------------------------------|------------------------------------------------------------------------------------------------|
| `* * *`  | new user                                    | see usage instructions                 | refer to instructions when I forget how to use the App                                         |
| `* * *`  | user                                        | list all people                        | see all people I have added                                                                    |
| `* * *`  | user                                        | add a new person                       |                                                                                                |
| `* * *`  | user                                        | delete a person                        | remove person entries that I no longer need                                                    |
| `* * *`  | user                                        | edit a person                          | modify the details of a person anytime                                                         |
| `* * *`  | user                                        | find a person by name                  | locate details of persons without having to go through the entire list                         |
| `* * *`  | user                                        | lookup associated articles to a person | find all articles related to that person                                                       |
| `* * *`  | user with many persons in the address book  | sort persons by name                   | locate a person easily                                                                         |
| `* * *`  | user                                        | clear all person entries               | remove all template or unwanted person data                                                    |
| `* *`    | user                                        | hide private contact details           | minimize chance of someone else seeing them by accident                                        |
| `* * *`  | user                                        | list all articles                      | see all articles I have added                                                                  |
| `* * *`  | user                                        | add a new article                      |                                                                                                |
| `* * *`  | user                                        | delete an article                      | remove article entries that I no longer need                                                   |
| `* * *`  | user                                        | edit an article                        | modify the details of an article anytime                                                       |
| `* * *`  | user                                        | find an article by headline            | locate details of articles without having to go through the entire list                        |
| `* * *`  | user                                        | filter articles by common attributes   | locate details of articles with common attributes without having to go through the entire list |
| `* * *`  | user                                        | remove filter for articles             | display the complete list of articles again after the filter is no longer needed               |
| `* * *`  | user                                        | lookup associated people to an article | find all people related to that article                                                        |
| `* * *`  | user with many articles in the address book | sort articles by date                  | locate the most recent articles easily                                                         |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `PressPlanner` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC01 - List all people**

**MSS**
1. User requests to list all people.
1. PressPlanner lists out all people.

   Use case ends.

**Use case: UC02 - Add a person**

**MSS**

1. User requests to add a person.
1. PressPlanner adds the person.
1. PressPlanner shows the added person to user.

   Use case ends.

**Extensions**

* 1a. Command was invalid.
  
  * 1a1. PressPlanner shows an error message.

    Use case resumes at step 1.


**Use case: UC03 - Delete a person**

**MSS**

1. User requests to list persons.
1. AddressBook shows a list of persons.
1. User requests to delete a specific person in the list.
1. AddressBook deletes the person.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. AddressBook shows an error message.

      Use case resumes at step 2.

**Use case: UC04 - Edit a person**

**MSS**

1. User requests to ***list all persons (UC01)***.
1. User requests to edit a specific person in the list
   by providing at least one change to an attribute of the article.
1. PressPlanner updates the article with the changes requested.
1. PressPlanner shows the updated article to user.

   Use case ends.

**Extensions**

* 2a. The given index is invalid.

    * 2a1. PressPlanner shows an error message.

      Use case resumes at step 2.

**Use case: UC05 - Find people**

**MSS**

1. User requests to find people with names containing given keywords.
1. PressPlanner displays a filtered list of people found,
   each having a name containing at least one of the given keywords.

   Use case ends.

**Extensions**

* 1a. No keywords are specified.

    * 1a1. PressPlanner shows an error message.

      Use case resumes at step 1.
  


**Use case: UC06 - Lookup associated articles for a person**

**MSS**

1. User requests to ***list all people (UC01)***.
1. User requests to lookup associated articles for a specific person in the list.
1. PressPlanner displays a filtered list of articles found,
   each having the person as a contributor or interviewee.

   Use case ends.

**Extensions**

* 2a. The given index is invalid.

    * 2a1. PressPlanner shows an error message.

      Use case resumes at step 2.
* 3a. The list is empty as there are no articles associated with the person.

  Use case ends.

**Use case: UC07 - Sort people by their names**

**MSS**

1. User requests to ***list all people (UC01)***.
1. User requests to sort people by their names.
1. PressPlanner sorts the people by their names in ascending alphabetical order and displays the sorted list of people.

   Use case ends.

**Extensions**

* 1a. The list is empty.

  Use case ends.


* 1b. The list is already sorted before and no ***edits to a person (UC04)*** modifies a person's name and changes that person's relative alphabetical ordering in the list or ***adding of a person (UC02)*** which results in that person not being ordered with respect to the rest of the list were performed afterwards.

  Use case ends.


* 2a. Invalid sorting attribute is given.

    * 2a1. PressPlanner shows an error message.

      Use case resumes at step 2.


**Use case: UC08 - Filter people**

**MSS**
1. User requests to filter people by tag.
1. PressPlanner returns a filtered list of people,
 all of whom have the matching tag.

    Use case ends.

**Extensions**

* 1a. User enters a non-alphanumeric tag.

    * 1a1. PressPlanner shows an error message.

      Use case resumes at step 1.

**Use case: UC09 - List all articles**

**MSS**
1. User requests to list articles.
1. PressPlanner lists out all articles.

   Use case ends.

**Use case: UC10 - Add an article**

**MSS**
1. User requests to add article.
1. PressPlanner adds article.
1. PressPlanner displays success message to User.

    Use case ends.

**Extensions**

* 1a. Command was invalid.
  
  * 1a1. PressPlanner shows an error message.

    Use case resumes at step 1.

**Use case: UC11 - Delete an article**

**MSS**

1. User requests to ***list all articles (UC09)***.
1. User requests to delete a specific article in the list.
1. PressPlanner deletes the article.
1. PressPlanner shows delete success message to user.

   Use case ends.

**Extensions**

* 2a. The given index is invalid.

    * 2a1. PressPlanner shows an error message.

      Use case resumes at step 2.

**Use case: UC12 - Edit an article**

**MSS**

1. User requests to ***list all articles (UC09)***.
1. User requests to edit a specific article in the list
   by providing at least one change to an attribute of the article.
1. PressPlanner updates the article with the changes requested.
1. PressPlanner shows the updated article to user.

   Use case ends.

**Extensions**

* 2a. The given index is invalid.

    * 2a1. PressPlanner shows an error message.

      Use case resumes at step 2.


* 2b. No changes to an attribute of the article is specified.

    * 2b1. PressPlanner shows an error message.

      Use case resumes at step 2.

**Use case: UC13 - Find articles**

**MSS**

1. User requests to find articles with headlines containing given keywords.
1. PressPlanner displays a filtered list of articles found,
 each having a headline containing at least one of the given keywords.

   Use case ends.

**Extensions**

* 1a. No keywords are specified.

    * 1a1. PressPlanner shows an error message.

      Use case resumes at step 1.



**Use case: UC14 - Filter articles**

**MSS**

1. User requests to filter articles by status, date of publication or tag.
1. PressPlanner displays a filtered list of articles,
 all of which fits the user's criteria.

    Use case ends

**Extensions**

* 1a. User gives an invalid status, tag or date.

    * 1a1. PressPlanner shows an error message
        
      Use case resumes at step 1.


* 1b. User omits any prefix.
    
    * 1b1.PressPlanner shows an error message
    
       Use case resumes at step 1.
     


**Use case: UC15 - Lookup associated people for an article**

**MSS**

1. User requests to ***list all articles (UC09)***.
1. User requests to lookup associated persons for a specific article in the list.
1. PressPlanner displays a filtered list of persons found,
   each featuring in the article as a contributor or interviewee.

   Use case ends.

**Extensions**

* 2a. The given index is invalid.

    * 2a1. PressPlanner shows an error message.

      Use case resumes at step 2.
  
* 3a. The list is empty as there are no persons associated with the article.

  Use case ends.

**Use case: UC16 - Sort articles by their date**

**MSS**

1. User requests to ***list all articles (UC09)***.
1. User requests to sort articles by their dates.
1. PressPlanner sorts the articles by their dates in descending chronological order and displays the sorted list of articles.

   Use case ends.

**Extensions**

* 1a. The list is empty.

  Use case ends.

* 1b. The list is already sorted before and no ***edits to an article (UC12)*** modifies an article's date and changes that article's relative chronological ordering in the list or ***adding of an article (UC10)*** which results in that article not being ordered with respect to the rest of the list were performed afterwards.

  Use case ends.


* 2a. Invalid sorting attribute is given.

    * 2a1. PressPlanner shows an error message.

      Use case resumes at step 2.



**Use case: UC17 - Open Webpage of Articles**

**MSS**

1. User requests to ***list all articles (UC09)***.
1. User requests to open webpage of a certain article.
1. PressPlanner opens a browser with the URL of the article.

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
1. Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
1. Should be able to hold up to 1000 articles without a noticeable sluggishness in performance for typical usage.
1. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
1. A user should see either a success message which represents a successful execution of a command, or an error message indicating that the command did not execute successfully for any user command supplied by the user.
1. If a command should fail, the underlying person or article data stored in PressPlanner should not be modified in any way not clearly visible to the user, to prevent the user from being oblivious to such changes if needed at all.  

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Story**: A story written by interviewing the person
* **Tag**: Additional information about a person or an article that the user can use to come up with his or her own classification system.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Sorting people by their names

1. Sorting people after inserting a person

    1. Prerequisites: There are 6 person entries in PressPlanner on first time launch, already in ascending alphabetical order, perform the following testcases in order.

    1. Test case: `add n/Aaron Tan p/82927320 e/a@gmail.com a/ Blk 123 Jurong Ring Road, #01-123` followed by `sort n/`<br>
       Expected: The person entries are sorted by their names in ascending alphabetical order. The person named `"Aaron Tan"` should be the first entry. Timestamp in the status bar is updated.

    1. Test case: `add n/Annie Lee p/82927320 e/a@gmail.com a/ Blk 123 Jurong Ring Road, #01-123` followed by `sort N/`<br>
       Expected: The person entries are sorted by their names in ascending alphabetical order. The person named `"Annie Lee"` should now be the third entry, after the person named `"Alex Yeoh"`. Timestamp in the status bar is updated.

    1. Test case: `add n/Zachery Tan p/82927320 e/a@gmail.com a/ Blk 123 Jurong Ring Road, #01-123` followed by `sort z/`<br>
       Expected: No reordering of people is done. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect sort person commands to try: `sort`, `sort x`, `...` (where x is anything that is not `n/` or `N/`)<br>
       Expected: Similar to previous.


### Adding an article

1. Adding an article 
    
    1. Test case: `add -a h/Article1 c/Author1 i/Interviewee1 t/Science d/01-01-2019 s/PUBLISHED`<br>
       Expected: Article1 is added to the list. Details of the added article shown in the status message.

    1. Test case: `add -a h/Article2 d/01-01-2021 s/PUBLISHED`<br>
       Expected: Article2 is added to the list. Details of the added article shown in the status message.

    1. Test case: `add -a h/Article3 c/Author3 s/DRAFT`<br>
       Expected: Error message is shown. Article3 is not added to the list.

### Deleting an article

1. Deleting an article while all article are being shown

   1. Prerequisites: List all articles using the `list -a` command. Multiple articles in the list.

   1. Test case: `delete -a 1`<br>
      Expected: First article is deleted from the list. Details of the deleted article shown in the status message.
   
   1. Test case: `delete -a 0`<br>
      Expected: No article is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete -a`, `delete -a x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

### Editing an article

1. Edit an article while all articles are being shown

   1. Prerequisites: List all articles using the `list -a` command. Multiple articles in the list.

   1. Test case: `edit -a 1 h/Article1`<br>
      Expected: First article is edited to Article1. Details of the edited article shown in the status message.

   1. Test case: `edit -a 0 h/Article1`<br>
      Expected: No article is edited. Error details shown in the status message.

   1. Other incorrect edit commands to try: `edit -a`, `edit -a x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

### Finding articles

1. Finding articles by their headlines using keywords

    1. Prerequisites: There is 1 article entry in PressPlanner on first time launch, perform the following testcases in order after adding the following articles provided as add article commands.
        1. `add -a h/one d/01-01-2001 s/draft`
        1. `add -a h/one two d/01-01-2001 s/draft`
        1. `add -a h/one two three d/01-01-2001 s/draft`

    1. Test case: `find -a one`<br>
       Expected: The only article with the headline `one` is shown in the list of articles. The status message shows the number of articles found. Timestamp in the status bar is updated.

    1. Test case: `find -a TWO`<br>
       Expected: Two articles are shown with headlines `one two` and `one two three`. The status message shows the number of articles found. Timestamp in the status bar is updated.

    1. Test case: `find -a thre`<br>
       Expected: No articles are found. The status message shows the number of articles found is `0`. Timestamp in the status bar is updated.


### Filtering through articles
1. Filtering through articles.
    1. Prerequisites: Populate PressPlanner with sufficient articles. You may use the following add commands:<br>
     
    1. Use these commands to populate PressPlanner.<br>
       `add -a h/Test-1 c/Author1 i/Interviewee1 t/Science d/01-01-2019 s/PUBLISHED`<br>
       `add -a h/Test-2 c/Author2 i/Interviewee2 d/01-01-2021 s/PUBLISHED`<br>
       `add -a h/Test-3 c/Author3  d/01-01-2019 s/DRAFT`<br>

    1. Test case: `filter -a s/ st/ en/ t/`<br>
        Expected:There will be no change in displayed articles.

    1. Test case: `filter -a s/DRAFT st/ en/ t/`<br>
        Expected: Only articles with draft status will be displayed.

    1. Test case: `filter -a s/ st/01-01-2020 en/12-12-2022 t/`<br>
        Expected: Only articles published between 01-01-2020 and 12-12-2022 will be displayed.
    
    1. Test case: `filter -a s/ st/ en/ t/Science`<br>
       Expected: Only articles with the tag `Science` will be displayed.

    1. Test case: `filter -a s/ st/`<br>
        Expected: An error informing the user that the command format is incorrect will be shown.

    1. Test case: `filter -a s/ st/ en/ t/non-alphanumeric`<br>
        Expected: An error informing the user that tags only consisting of alphanumeric characters will be shown.

    1. Test case: `filter -a s/ st/01-01-2020 en/01-01-2001 t/`<br>
    Expected: An error informing the user that start dates must come before end dates will be shown.

### Lookup a person & article

1. Lookup person/article after adding a person/article

    1. Prerequisites: Assume non-empty list of persons and articles. Change index numbers as needed.

    1. Test case: `add n/Alice1 p/12345678 e/alice@email.com a/Blk 424 #11-0536 Yishun Ring Road`<br> `lookup 1`<br>
       Expected: Alice1 is added to the list. An empty list of articles associated with Alice1 is shown.

    1. Testcase: `add -a h/Article1 c/Alice1 d/11-09-2021 s/DRAFT`<br> `lookup -a 1`<br>
       Expected: Article1 is added to the list. Alice1 is shown as a list of persons associated with Article1.

    1. Lookup person: `lookup 1`<br>
       Expected: Article1 is shown as a list of articles associated with Alice1.

    1. Test case: `lookup 0`<br>
       Expected: Error message is shown.

    1. Test case: `lookup -a 0`<br>
       Expected: Error message is shown.

    1. Delete the person and article added in the prerequisites. Then repeat the above testcases by altering the orders such that the article is added first and then person. The commands should differ accordingly.

1. Lookup after editing person and article

    1. Test case: `edit 1 n/Alice2`<br>
       Expected: Alice is edited to Alice2. This is reflected in the article Article0 contributor tag as well.

    1. Test case: `edit -a 1 h/Article1`<br>
       Expected: Article0 is edited to Article1.

    1. Test case: `lookup 1`<br>
       Expected: Article1 is shown as a list of articles associated with Alice2.

    1. Test case: `lookup -a 1`<br>
       Expected: Alice2 is shown as a list of persons associated with Article1.

### Sorting articles by their dates

1. Sorting articles after inserting an article with a date of `"0X-01-2100"` replacing `"X"` with a number starting from 1 up to 9

    1. Prerequisites: There is 1 article entry in PressPlanner on first time launch, perform the following testcases in order.

    1. Test case: `add h/Article1 d/01-01-2100 s/draft` followed by `sort -a d/`<br>
       Expected: The article entries are sorted by their dates in descending chronological order. The article with the headline `Article1` and date `"01-01-2100"` should be the first entry. Timestamp in the status bar is updated.

    1. Test case: `add h/Article2 d/02-01-2100 s/draft` followed by `sort -a D/`<br>
       Expected: The article entries are sorted by their dates in descending chronological order. The article  the headline `Article2` and date `"02-01-2100"` should be the first entry, before `Article1` with the date `"01-01-2100"`. Timestamp in the status bar is updated.

    1. Test case: `add h/Article3 d/03-01-2100 s/draft` followed by `sort -a z/`<br>
       Expected: No reordering of articles is done. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect sort article commands to try: `sort -a`, `sort -a x`, `...` (where x is anything that is not `d/` or `D/`)<br>
       Expected: Similar to previous.

### Opening Links

1. Opening a link to an article

    1. Create articles using `add -a h/Article1 d/20-03-2024 s/draft l/https://www.google.com`, `add -a h/Article2 d/20-03-2024 s/draft l/https://www.facebook.com/` and `add -a h/Article3 d/20-03-2024 s/draft l/` commands.

    1. Test case: `add -a h/Article1 d/20-03-2024 s/draft l/https://www.google.com`, followed by click on the link button of the first article.<br>
       Expected: The link to google is opened in the default web browser.

    1. Test case: `add -a h/Article2 d/20-03-2024 s/draft l/https://www.facebook.com/`, followed by click on the link button of the last article.<br>
       Expected: The link to facebook is opened in the default web browser.

    1. Test case: `add -a h/Article3 d/20-03-2024 s/draft l/`, followed by click on the link button of an article that does not have a link.<br>
       Expected: Nothing happens.


1. _{ more test cases …​ }_
### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

--------------------------------------------------------------------------------------------------------------------
## **Planned Enhancements**

1. **Make URL failure more explicit**: Currently when a URL cannot be opened the app shows that by not opening anything. This can be improved by showing an error message.


1. **The filter command can work for individual prefixes**: Currently the filter command only works for all prefixes. It can be improved by allowing the user to filter by individual prefixes.


1. **Automatically sort persons by their names in ascending alphabetical ordering**: Currently whenever the user makes an edit to a person's name or adds new person entries which may result in a violation of the previous ordering, the user would have to execute the `sort n/` command to re-sort the person entries. It can be improved by automatically sorting people whenever new entries, or certain edits to them are made to reduce such inconveniences to the user.


1. **Automatically sort articles by their publication dates in descending order**: Currently whenever the user makes an edit to an article's publication date or adds new article entries which may result in a violation of the previous ordering, the user would have to execute the `sort -a d/` command to re-sort the articles. It can be improved by automatically sorting articles whenever new entries, or certain edits to them are made to reduce such inconveniences to the user.


1. **Provide alternative methods to create associations between persons and articles**: Currently the user can only create associations between persons and articles when adding/editing the persons or articles. It might not always be desirable to create associations when adding/editing persons or articles. This can be improved by providing an alternative method to create associations between persons and articles by using IDs unique to each person and article. Instead of using names to create associations, the user can use the IDs to create associations between persons and articles.


1. **Allow the user to filter people**: Currently the user can only filter articles. It can be improved by allowing the user to filter people as well.


1. **Allow editing or deleting at any index for duplicate articles**: Currently, the user can only edit or delete the first article when there are multiple draft articles with the identical attributes. It can be improved by allowing the user to edit or delete any article with the same headline.


1. **Enhance prefix validation**: Currently, the user can use duplicate prefixes for the same attribute when adding or editing persons or articles, or even add incorrect prefixes in their commands. It can be improved by enhancing the prefix validation to prevent the user from using duplicate prefixes for the same attribute and to guide the user to use the correct prefixes in their commands.


1. **Enable greater ease in adding/editing/deleting cumulative attributes**: Currently these tags for both person and article, and contributor, interviewee and outlets for articles are not cumulative. If user wants to add one more, they will have to add the existing ones at the same time using edit. They cannot also delete any single one, but have to clear all existing ones and add the ones that they want to remain again using edit. The enhancement can let there be a index for those attributes enabling the user to add/edit/delete them with greater ease.


1. **Allow non-alphanumeric inputs for tags**: Currently, the user can only use alphanumeric characters for tags. It can be improved by allowing the user to use non-alphanumeric characters for tags.
