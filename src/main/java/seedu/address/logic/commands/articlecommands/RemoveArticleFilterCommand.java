package seedu.address.logic.commands.articlecommands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_ARTICLES;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Removes filter so all articles are displayed
 */
public class RemoveArticleFilterCommand extends ArticleCommand {
    public static final String COMMAND_WORD = "rmfilter";
    public static final String COMMAND_PREFIX = "-a";
    private static final String MESSAGE_SUCCESS = "Filters have been removed. All articles will be displayed again.";
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        model.getFilter().updateFilter(PREDICATE_SHOW_ALL_ARTICLES);
        model.updateFilteredArticleList(PREDICATE_SHOW_ALL_ARTICLES);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
