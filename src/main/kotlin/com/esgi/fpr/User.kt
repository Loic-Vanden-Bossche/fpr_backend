import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "people")
class Person {
    @Id
    var id: Long? = null
        private set

    @Column(name = "name")
    var name: String? = null
        private set

    @Column(name = "role")
    var role: String? = null
        private set

    constructor()
    constructor(id: Long?, name: String?, role: String?) {
        this.id = id
        this.name = name
        this.role = role
    }
}