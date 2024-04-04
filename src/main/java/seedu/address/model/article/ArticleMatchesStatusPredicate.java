package seedu.address.model.article;


import java.util.function.Predicate;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.article.exceptions.InvalidStatusException;

/**
 * Ensures that an {@code Article}'s {@code Status} matches the given status
 */
public class ArticleMatchesStatusPredicate implements Predicate<Article> {
    private Enum<Article.Status> status;

    /**
     * Constructs an ArticleMatchesStatusPredicate
     * @param s The string representation of the status
     * @throws InvalidStatusException thrown when status entered is invalid.
     */
    public ArticleMatchesStatusPredicate(String s) throws InvalidStatusException {
        try {
            status = ParserUtil.parseStatus(s);
        } catch (ParseException e) {
            throw new InvalidStatusException();
        }
    }

    @Override
    public boolean test(Article article) {
        //Check that article is not null.
        assert (article instanceof Article);
        return this.status.equals(article.getStatus());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TitleContainsKeywordsPredicate)) {
            return false;
        }

        ArticleMatchesStatusPredicate otherArticleMatchesStatusPredicate = (ArticleMatchesStatusPredicate) other;
        return this.status.equals(otherArticleMatchesStatusPredicate.getStatus());
    }
    public Enum<Article.Status> getStatus() {
        return this.status;
    }
}
