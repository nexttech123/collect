package org.odk.collect.android.mainmenu

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import org.odk.collect.android.R
import org.odk.collect.android.databinding.MainMenuButtonBinding
import org.odk.collect.androidshared.system.ContextUtils.getThemeAttributeValue
import org.odk.collect.androidshared.ui.multiclicksafe.MultiClickGuard

class MainMenuButton(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    constructor(context: Context) : this(context, null)

    private val binding = MainMenuButtonBinding.inflate(LayoutInflater.from(context), this, true)
    private val badge: BadgeDrawable
    private var highlightable: Boolean = false

    init {
        context.withStyledAttributes(attrs, R.styleable.MainMenuButton) {
            val buttonIcon = this.getResourceId(R.styleable.MainMenuButton_icon, 0)
            val buttonName = this.getString(R.styleable.MainMenuButton_name)
            highlightable = this.getBoolean(R.styleable.MainMenuButton_highlightable, false)

            // Configure le texte au-dessus de l'icône
            binding.name.text = buttonName
            binding.icon.setImageResource(buttonIcon)

            // Appeler la fonction pour gérer le dépassement du texte
            //enableTextOverflowHandling()
        }

        badge = BadgeDrawable.create(context).apply {
            backgroundColor = getThemeAttributeValue(context, com.google.android.material.R.attr.colorPrimary)
            badgeGravity = BadgeDrawable.BOTTOM_END
        }

        // Supprime les coins arrondis du bouton
        background = null
    }

    val text: String
        get() = binding.name.text.toString()

    override fun performClick(): Boolean {
        return MultiClickGuard.allowClick(context.getString(R.string.main_menu_screen)) && super.performClick()
    }

    fun setNumberOfForms(number: Int) {
        // Si le nombre est inférieur à 1, pas de texte affiché
        binding.number.text = if (number < 1) {
            ""
        } else {
            number.toString()  // Si le nombre est supérieur à 0, afficher le texte
        }

        @ExperimentalBadgeUtils
        if (highlightable) {
            if (number > 0) {
                // Si le nombre est supérieur à 0, appliquer le badge et le style
                binding.icon.viewTreeObserver.addOnGlobalLayoutListener {
                    BadgeUtils.attachBadgeDrawable(badge, binding.icon)  // Afficher le badge
                }
                binding.name.setTypeface(binding.name.typeface, Typeface.BOLD)  // Mettre le texte en gras
                binding.number.setTypeface(binding.number.typeface, Typeface.BOLD)  // Mettre le nombre en gras
            } else {
                // Si le nombre est vide (ou 0), retirer le style et le badge
                binding.icon.viewTreeObserver.addOnGlobalLayoutListener {
                    BadgeUtils.detachBadgeDrawable(badge, binding.icon)
                    binding.number.visibility= INVISIBLE// Retirer le badge
                }
                // Remettre le style de police par défaut
                binding.name.typeface = Typeface.DEFAULT
                binding.number.typeface = Typeface.DEFAULT
                // Retirer le background et les styles visuels si nécessaires
                binding.number.background = null  // Enlever le background si applicable
                binding.name.background = null    // Retirer également le background de la name si nécessaire
            }
        }
    }



    fun enableTextOverflowHandling() {
        binding.name.viewTreeObserver.addOnGlobalLayoutListener {
            val textViewWidth = binding.name.width
            val textWidth = binding.name.paint.measureText(binding.name.text.toString())

            // Si le texte dépasse la largeur de la TextView
            if (textWidth > textViewWidth) {
                // Activer le défilement horizontal pour gérer le dépassement
                binding.name.isSingleLine = true  // S'assurer que le texte reste sur une ligne
                binding.name.ellipsize = TextUtils.TruncateAt.MARQUEE  // Activer le défilement (marquee)
                binding.name.marqueeRepeatLimit = -1 // Défilement infini
                binding.name.setHorizontallyScrolling(true)
                binding.name.isSelected = true // Le texte défilera lorsque la vue est sélectionnée

            } else {
                // Si le texte ne dépasse pas, désactiver le défilement et afficher normalement
                binding.name.isSingleLine = false
                binding.name.ellipsize = null  // Aucun ellipsize, afficher le texte normalement
                binding.name.setHorizontallyScrolling(false)
            }
        }
    }


}
