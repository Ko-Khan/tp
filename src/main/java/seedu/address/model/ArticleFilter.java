package seedu.address.model;

import java.util.function.Predicate;

import seedu.address.model.article.Article;

/**
 * Use to filter through ArticleBook
 */
public class ArticleFilter implements Filter {
    private Predicate<Article> statusPredicate;
    public void updateFilter() {

    }
}
