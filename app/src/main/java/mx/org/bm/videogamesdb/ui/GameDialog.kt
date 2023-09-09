package mx.org.bm.videogamesdb.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.org.bm.videogamesdb.R
import mx.org.bm.videogamesdb.application.VideogamesDBApp
import mx.org.bm.videogamesdb.data.GameRepository
import mx.org.bm.videogamesdb.data.db.model.GameEntity
import mx.org.bm.videogamesdb.databinding.GameDialogBinding
import java.io.IOException

class GameDialog(
    private  var isNewGame: Boolean = true,
    private  var game:GameEntity = GameEntity(
        title = "",
        genre = "",
        genreId = 0,
        developer = ""
    ), private  val updateUI: () -> Unit,
    private  val message: (Int) -> Unit
): DialogFragment() {

    private var _binding: GameDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: Dialog

    private  var saveButton: Button? = null

    private lateinit var repository: GameRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = GameDialogBinding.inflate(requireActivity().layoutInflater)

        repository = (requireContext().applicationContext as VideogamesDBApp).repository

        builder = AlertDialog.Builder(requireContext())

        setupSpinnerOptions()

        binding.apply {
            tietTitle.setText(game.title)
            tietDeveloper.setText(game.developer)
            genreSpinner.setSelection(game.genreId)
        }

        val array: Array<String> = resources.getStringArray(R.array.genre_opts)

        dialog = if(isNewGame){
            buildDialog("Guardar", "Cancelar", {
                // Guardar
                game.title = binding.tietTitle.text.toString()
                game.genre = array[binding.genreSpinner.selectedItemPosition]
                game.developer = binding.tietDeveloper.text.toString()
                game.genreId = binding.genreSpinner.selectedItemPosition

                try{
                    lifecycleScope.launch {
                        repository.insertGame(game)

                    }

                    message(R.string.save_message)
                    //Toast.makeText(requireContext(), "Juego guardado correctamente", Toast.LENGTH_LONG).show()

                    updateUI()
                }catch(e: IOException){
                    //message("Error al guardar el juego")
                    //Toast.makeText(requireContext(), "Error al guardar el juego", Toast.LENGTH_LONG).show()
                }
            }, {
                // Cancelar
            })
        }else{
            buildDialog("Actualizar", "Borrar", {
                // Update
                game.title = binding.tietTitle.text.toString()
                game.genre = array[binding.genreSpinner.selectedItemPosition]
                game.developer = binding.tietDeveloper.text.toString()
                game.genreId = binding.genreSpinner.selectedItemPosition

                try{
                    lifecycleScope.launch {
                        repository.updateGame(game)
                    }

                    message(R.string.update_message)
                    //Toast.makeText(requireContext(), "Juego actualizado correctamente", Toast.LENGTH_LONG).show()

                    updateUI()
                }catch(e: IOException){
                    //message("Error al actualizar el juego")
                    Toast.makeText(requireContext(), "Error al actualizar el juego", Toast.LENGTH_LONG).show()
                }

            }, {
                // Delete

                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmación")
                    .setMessage("¿Seguro de borrar el juego ${game.title}?")
                    .setPositiveButton("Aceptar"){_,_ ->
                        try{
                            lifecycleScope.launch {
                                repository.deleteGame(game)
                            }

                            message(R.string.delete_message)
                            //Toast.makeText(requireContext(), "Juego eliminado correctamente", Toast.LENGTH_LONG).show()

                            updateUI()
                        }catch(e: IOException){
                            //message("Error al eliminar el juego")
                            //Toast.makeText(requireContext(), "Error al eliminar el juego", Toast.LENGTH_LONG).show()
                        }
                    }.setNegativeButton("Cancelar"){dialog, _ ->
                        dialog.dismiss()
                    }.create()
                    .show()


            })
        }

        return dialog
    }

    private fun setupSpinnerOptions(){
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.genre_opts,
            android.R.layout.simple_spinner_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.genreSpinner.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        val alertDialog = dialog as AlertDialog
        saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        saveButton?.isEnabled = false

        binding.tietTitle.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })

        binding.tietDeveloper.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })
    }

    private fun validateFields(): Boolean{
        return (binding.tietTitle.text.toString().isNotEmpty() && binding.tietDeveloper.text.toString().isNotEmpty())
    }

    private fun buildDialog(btn1Text: String, btn2Text:String,
                            positiveButton: () -> Unit, negativeButton: () -> Unit): Dialog =
        builder.setView(binding.root)
            .setTitle("Juego")
            .setPositiveButton(btn1Text){dialog, _ ->
                positiveButton()
            }
            .setNegativeButton(btn2Text){dialog, _ ->
                negativeButton()
            }.create()
}