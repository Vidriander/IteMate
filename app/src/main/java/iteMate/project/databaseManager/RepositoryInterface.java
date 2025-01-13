package iteMate.project.databaseManager;

import iteMate.project.databaseManager.listeners.OnMultipleDocumentsFetchedListener;
import iteMate.project.databaseManager.listeners.OnSingleDocumentFetchedListener;
import iteMate.project.model.DocumentEquivalent;

public interface RepositoryInterface<T extends DocumentEquivalent> {

    void getOneDocumentFromDatabase(String documentId, OnSingleDocumentFetchedListener<T> listener);

    void getAllDocumentsFromDatabase(OnMultipleDocumentsFetchedListener<T> listener);

    void addDocumentToDatabase(T element);

    void updateDocumentInDatabase(T document);

    void deleteDocumentFromDatabase(T document);
}
