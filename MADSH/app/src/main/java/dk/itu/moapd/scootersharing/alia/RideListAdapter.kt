package dk.itu.moapd.scootersharing.alia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class RideListAdapter(context: Context, private var resource: Int, rides: List<Scooter>)
    : ArrayAdapter<Scooter>(context, R.layout.list_rides, rides) {

    private class ViewHolder(view: View) {
        val name: TextView = view.findViewById(R.id.ride_name)
        val location: TextView = view.findViewById(R.id.ride_location)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val entity = getItem(position)

        viewHolder.name.text = entity?.name
        viewHolder.location.text = entity?.location

        view?.tag = viewHolder
        return view!!
    }
}