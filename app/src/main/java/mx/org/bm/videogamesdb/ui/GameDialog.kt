package mx.org.bm.videogamesdb.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Update
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
        developer = ""
    ), private  val updateUI: () -> Unit,
    private  val message: (String) -> Unit
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

        binding.apply {
            tietTitle.setText(game.title)
            tietGenre.setText(game.genre)
            tietDeveloper.setText(game.developer)
        }

        dialog = if(isNewGame){
            buidDialog("Guardar", "Cancelar", {
                // Guardar
                game.title = binding.tietTitle.text.toString()
                game.genre = binding.tietGenre.text.toString()
                game.developer = binding.tietDeveloper.text.toString()

                try{
                    lifecycleScope.launch {
                        repository.insertGame(game)

                    }

                    message("Juego guardado correctamente")
                    //Toast.makeText(requireContext(), "Juego guardado correctamente", Toast.LENGTH_LONG).show()

                    updateUI()
                }catch(e: IOException){
                    message("Error al guardar el juego")
                    //Toast.makeText(requireContext(), "Error al guardar el juego", Toast.LENGTH_LONG).show()
                }
            }, {
                // Cancelar
            })
        }else{
            buidDialog("Actualizar", "Borrar", {
                // Update
                game.title = binding.tietTitle.text.toString()
                game.genre = binding.tietGenre.text.toString()
                game.developer = binding.tietDeveloper.text.toString()

                try{
                    lifecycleScope.launch {
                        repository.updateGame(game)
                    }

                    message("Juego actualizado correctamente")
                    //Toast.makeText(requireContext(), "Juego actualizado correctamente", Toast.LENGTH_LONG).show()

                    updateUI()
                }catch(e: IOException){
                    message("Error al actualizar el juego")
                    //Toast.makeText(requireContext(), "Error al actualizar el juego", Toast.LENGTH_LONG).show()
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

                            message("Juego eliminado correctamente")
                            //Toast.makeText(requireContext(), "Juego eliminado correctamente", Toast.LENGTH_LONG).show()

                            updateUI()
                        }catch(e: IOException){
                            message("Error al eliminar el juego")
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

        binding.tietGenre.addTextChangedListener(object: TextWatcher{
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
        return (binding.tietTitle.text.toString().isNotEmpty() && binding.tietGenre.text.toString().isNotEmpty() && binding.tietDeveloper.text.toString().isNotEmpty())
    }

    private fun buidDialog(btn1Text: String, btn2Text:String, positiveButton: () -> Unit, negativeButton: () -> Unit): Dialog =
        builder.setView(binding.root)
            .setTitle("Juego")
            .setPositiveButton(btn1Text){dialog, _ ->
                positiveButton()
            }
            .setNegativeButton(btn2Text){dialog, _ ->
                negativeButton()
            }.create()
}