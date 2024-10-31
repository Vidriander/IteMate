package iteMate.project.repositories.listeners;

public interface OnSingleDocumentFetchedListener<T> extends Listener{

    void onDocumentFetched(T document);
}
