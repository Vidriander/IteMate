package iteMate.project.repositories;

public interface OnSingleDocumentFetchedListener<T> extends Listener{

    void onDocumentFetched(T document);
}
