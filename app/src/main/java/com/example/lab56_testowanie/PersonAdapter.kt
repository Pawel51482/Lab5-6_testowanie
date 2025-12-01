package com.example.lab56_testowanie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab56_testowanie.databinding.ItemPersonBinding

class PersonAdapter(
    private val items: List<Person>
) : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    inner class PersonViewHolder(val binding: ItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = ItemPersonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PersonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val p = items[position]
        holder.binding.tvName.text = "${p.firstName} ${p.lastName}"
        holder.binding.tvDetails.text =
            "Data urodzenia: ${p.birthDate}\nTelefon: ${p.phone}\nE-mail: ${p.email}\nAdres: ${p.address}"
    }

    override fun getItemCount(): Int = items.size
}
