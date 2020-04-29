package kushal.application.covaupdates

import ir.mirrajabi.searchdialog.core.Searchable

public class SearchModel(var t : String) : Searchable {

    override fun getTitle(): String {
        return t
    }

    public fun setTitle(mstring : String) : SearchModel{
        t = mstring
        return this
    }

}