package iteMate.project.repositories.listeners;

import java.util.List;

public interface OnMultipleDocumentsFetchedListener<T> extends Listener {

    void onDocumentsFetched(List<T> documents);
}
