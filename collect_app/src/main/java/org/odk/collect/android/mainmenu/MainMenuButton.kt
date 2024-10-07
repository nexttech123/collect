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
            enableTextOverflowHandling()
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
        // Mettre à jour le texte du champ "number" selon la condition
        binding.number.text = if (number < 1) {
            ""
        } else {
            number.toString()
        }

        @ExperimentalBadgeUtils
        if (highlightable) {
            if (number > 0) {
                // Ajouter le badge et mettre en gras le texte si le nombre est supérieur à 0
                binding.name.setTypeface(binding.name.typeface, Typeface.BOLD)
                binding.number.setTypeface(binding.name.typeface, Typeface.BOLD)
                binding.number.setBackgroundResource(R.drawable.main_button_textview_background)
               // BadgeUtils.attachBadgeDrawable can be uncommented if needed
                binding.icon.viewTreeObserver.addOnGlobalLayoutListener {
                    BadgeUtils.attachBadgeDrawable(badge, binding.icon)
                }
            } else {
                // Détacher le badge et remettre les styles par défaut
                binding.icon.viewTreeObserver.addOnGlobalLayoutListener {
                    BadgeUtils.detachBadgeDrawable(badge, binding.icon)
                }
                binding.name.typeface = Typeface.DEFAULT
                binding.number.typeface = Typeface.DEFAULT
                binding.number.background = null  // Enlever le background
                binding.name.background = null    // Enlever également le background de "name" si nécessaire
            }
        }
    }

    //Function allows you to scroll the text if you go beyond the corresponding context
    fun enableTextOverflowHandling() {
        binding.name.viewTreeObserver.addOnGlobalLayoutListener {
            val textViewWidth = binding.name.width
            val textWidth = binding.name.paint.measureText(binding.name.text.toString())

            // Si le texte dépasse la largeur de la TextView
            if (textWidth > textViewWidth) {
                // Activer le défilement horizontal pour gérer le dépassement
                //binding.name.isSingleLine = true  // S'assurer que le texte reste sur une ligne
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
