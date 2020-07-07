sealed trait EitherImp[+L, +R] extends Product with Serializable {

  def isLeft: Boolean

  def isRight: Boolean

  /** Projects this `Either` as a `Right`.
   */
  def right: RightProjectionImp[L, R] = RightProjectionImp(this)

  /** The given function is applied if this is a `Right`.
   */
  def map[R1](f: R => R1): EitherImp[L, R1] = this match {
    case RightImp(b) => RightImp(f(b))
    case _ => this.asInstanceOf[EitherImp[L, R1]]
  }

  /** Binds the given function across `Right`.
   */
  def flatMap[L1 >: L, R1](f: R => EitherImp[L1, R1]): EitherImp[L1, R1] = this match {
    case RightImp(b) => f(b)
    case _ => this.asInstanceOf[EitherImp[L1, R1]]
  }

  /** Binds the given function across `Left`.
   */
  def leftFlatMap[L1, R1 >: R](f: L => EitherImp[L1, R1]): EitherImp[L1, R1] = this.left.flatMap(f)

  /** Projects this `Either` as a `Left`.
   */
  def left: LeftProjectionImp[L, R] = LeftProjectionImp(this)

  /** Maps the function argument through `Left`.
   */
  def leftMap[L1](f: L => L1): EitherImp[L1, R] = left.map(f)

}

case class RightImp[R](value: R) extends EitherImp[Nothing, R] {
  override def isLeft = false

  override def isRight = true
}

final case class RightProjectionImp[+L, +R](e: EitherImp[L, R]) {

  /** Returns the value from this `Right` or throws `java.util.NoSuchElementException` if this is a `Left`.
   */
  def get: R = e match {
    case RightImp(b) => b
    case _ => throw new NoSuchElementException("Either.right.get on Left")
  }
}

case class LeftImp[L](value: L) extends EitherImp[L, Nothing] {
  override def isLeft = true

  override def isRight = false
}

final case class LeftProjectionImp[+L, +R](e: EitherImp[L, R]) {
  /** Returns the value from this `Left` or throws `java.util.NoSuchElementException` if this is a `Right`.
   */
  def get: L = e match {
    case LeftImp(a) => a
    case _ => throw new NoSuchElementException("Either.left.get on Right")
  }

  /** Binds the given function across `Left`.
   */
  def flatMap[L1, R1 >: R](f: L => EitherImp[L1, R1]): EitherImp[L1, R1] = e match {
    case LeftImp(a) => f(a)
    case _ => e.asInstanceOf[EitherImp[L1, R1]]
  }

  /** Maps the function argument through `Left`.
   */
  def map[L1](f: L => L1): EitherImp[L1, R] = e match {
    case LeftImp(a) => LeftImp(f(a))
    case _ => e.asInstanceOf[EitherImp[L1, R]]
  }

}

// Right#apply, Left#apply
