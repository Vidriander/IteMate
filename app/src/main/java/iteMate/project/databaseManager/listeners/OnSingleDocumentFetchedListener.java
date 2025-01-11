package iteMate.project.databaseManager.listeners;

public interface OnSingleDocumentFetchedListener<T> extends Listener{

    void onDocumentFetched(T document);
}
