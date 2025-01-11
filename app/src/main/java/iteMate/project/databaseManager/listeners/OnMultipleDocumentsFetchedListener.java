package iteMate.project.databaseManager.listeners;

import java.util.List;

public interface OnMultipleDocumentsFetchedListener<T> extends Listener {

    void onDocumentsFetched(List<T> documents);
}
