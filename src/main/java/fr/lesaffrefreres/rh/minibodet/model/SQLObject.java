package fr.lesaffrefreres.rh.minibodet.model;

/**
 * This interface represent an object mapping an SQL entity.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public interface SQLObject {

    /**
     * Return the id of the mapped SQL entity.
     * @return the id of the mapped SQL entity.
     */
    public long getId();


    /**
     * If this object is not yet in the database, it is inserted. And then return the id of the inserted object.
     * @return the id of the inserted object.
     */
    public long create();
}
